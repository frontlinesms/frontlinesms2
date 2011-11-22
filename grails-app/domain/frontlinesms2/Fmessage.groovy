package frontlinesms2

import groovy.time.*


class Fmessage {
	String src
	String dst
	String text
	String contactName
	Date dateCreated
	Date dateReceived
	Date dateSent
	boolean contactExists
	MessageStatus status
	boolean read
	boolean deleted
	boolean starred
	boolean archived
	static belongsTo = [messageOwner:MessageOwner]

	static mapping = {
		sort dateCreated:'desc'
		sort dateReceived:'desc'
		sort dateSent:'desc'
		autoTimestamp false
	}

	def beforeInsert = {
		dateCreated = dateCreated ?: new Date()
		if(status == MessageStatus.INBOUND) dateReceived = dateReceived ?: new Date()
		else dateSent = dateSent ?: new Date()
		if(status == MessageStatus.INBOUND ? src : dst) updateContactName()
	}
	
	private String findContact(String number) {
		return Contact.findByPrimaryMobile(number)?.name ?: (Contact.findBySecondaryMobile(number)?.name ?: number)
	}
		
	def updateContactName() {
		def fetchContactName = { number ->
			Contact.withNewSession {
				return Contact.findByPrimaryMobile(number)?.name ?: (Contact.findBySecondaryMobile(number)?.name ?: number)
			}
		}
		contactExists = contactName && contactName != src && contactName != dst
		contactExists ?: (contactName = fetchContactName(status == MessageStatus.INBOUND ? src : dst))
	}
	
	static constraints = {
		src(nullable:true)
		dst(nullable:true)
		text(nullable:true)
		messageOwner(nullable:true)
		dateReceived(nullable:true)
		dateSent(nullable:true)
		status(nullable:true)
		contactName(nullable:true)
		contactExists(nullable:true)
		archived(nullable:true, validator: { val, obj ->
				if(val) {
					obj.messageOwner == null || (obj.messageOwner instanceof PollResponse && obj.messageOwner.poll.archived) || obj.messageOwner.archived
				} else {
					obj.messageOwner == null || (obj.messageOwner instanceof PollResponse && !obj.messageOwner.poll.archived) || (!(obj.messageOwner instanceof PollResponse) && !obj.messageOwner.archived)
				}
		})
	}
	
	static namedQueries = {
		inbox { getOnlyStarred=false, getOnlyArchived=false ->
			and {
				eq("deleted", false)
				eq("archived", getOnlyArchived)
				if(getOnlyStarred)
					eq("starred", true)
				eq("status", MessageStatus.INBOUND)
				isNull("messageOwner")
			}
		}
		sent { getOnlyStarred=false, getOnlyArchived=false ->
			and {
				eq("deleted", false)
				eq("archived", getOnlyArchived)
				eq("status", MessageStatus.SENT)
				isNull("messageOwner")
				if(getOnlyStarred)
					eq("starred", true)
			}
		}
		pending { getOnlyFailed=false ->
			and {
				eq("deleted", false)
				eq("archived", false)
				isNull("messageOwner")
				'in'("status", getOnlyFailed?
						[MessageStatus.SEND_FAILED]:
						[MessageStatus.SEND_PENDING, MessageStatus.SEND_FAILED])
			}
		}
		deleted { getOnlyStarred=false ->
			and {
				eq("deleted", true)
				eq("archived", false)
				if(getOnlyStarred)
					eq('starred', true)
			}
		}
		owned { getOnlyStarred=false, owners ->
			and {
				eq("deleted", false)
				'in'("messageOwner", owners)
				if(getOnlyStarred)
					eq("starred", true)
			}
		}
		unread {
			and {
				eq("deleted", false)
				eq("archived", false)
				eq("status", MessageStatus.INBOUND)
				eq("read", false)
				isNull("messageOwner")
			}
		}

		search { search -> 
			and {
				if(search.searchString) {
					ilike("text", "%${search.searchString}%")
				}
				if(search.contactString) {
					ilike("contactName", "%${search.contactString}%")
				} 
				if(search.group) {
					def groupMembersNumbers = search.group.getAddresses()?:[""] //otherwise hibernate fail to search 'in' empty list
					or {
						'in'("src", groupMembersNumbers)
						'in'("dst", groupMembersNumbers)
					}
				}
				if(search.status) {
					'in'('status', search.status.tokenize(",")*.trim().collect { MessageStatus.valueOf(it) })
				}
				if(search.owners) {
					'in'("messageOwner", search.owners)
				}
				if(search.startDate && search.endDate) {
					between("dateReceived", search.startDate, search.endDate.next())
				} else if (search.startDate) {	
					ge("dateReceived", search.startDate)
				} else if (search.endDate) {
					le("dateReceived", search.endDate.next())
				}
				if(search.customFields.find{it.value}) {
					def contactNameMatchingCustomField = CustomField.getAllContactNameMatchingCustomField(search.customFields)?:[''] //otherwise hibernate fail to search 'in' empty list
					'in'("contactName", contactNameMatchingCustomField)
				}
				if(!search.inArchive) {
					eq('archived', false)
				}
				eq('deleted', false)
			}
		}
		
		filterMessages { params ->
			def groupInstance = params.groupInstance
			def messageOwner = params.messageOwner
			def startDate = toStartOfDay(params.startDate)
			def endDate = toEndOfDay(params.endDate)
			def groupMembers = groupInstance?.getAddresses() ?: ''
			and {
				if(groupInstance) {
					'in'("src", groupMembers)
				}
				if(messageOwner) {
					'in'("messageOwner", messageOwner)
				}
				if(params.messageStatus) {
					'in'('status', params.messageStatus.collect { Enum.valueOf(MessageStatus.class, it)})
				}
				eq('deleted', false)
				or {
					and {
						between("dateReceived", startDate, endDate)
						eq("status", MessageStatus.INBOUND)
					}
					and {
						between("dateSent", startDate, endDate)
						eq("status", MessageStatus.SENT)
					}
				}
			}
		}
	}

	def getDisplayText() {
		def p = PollResponse.withCriteria {
			messages {
				eq('deleted', false)
				eq('archived', false)
				eq('id', this.id)
			}
		}

		p?.size()?"${p[0].value} (\"${this.text}\")":this.text
	}
	
	def getDisplayName() { 
		contactName
	}
	
	static def countUnreadMessages(isStarred) {
		Fmessage.unread().count()
	}
	
	static def countAllMessages(params) {
		def inboxCount = Fmessage.inbox.count()
		def sentCount = Fmessage.sent.count()
		def pendingCount = Fmessage.pending.count()
		def deletedCount = Fmessage.deleted.count()
		[inbox: inboxCount, sent: sentCount, pending: pendingCount, deleted: deletedCount]
	}

	static def hasFailedMessages() {
		Fmessage.countByStatus(MessageStatus.SEND_FAILED) > 0
	}
	
	static def getMessageOwners(activity) {
		activity instanceof Poll ? activity.responses : [activity]
	}

	// TODO should this be in a service?
	static def getMessageStats(params) {
		def messages = Fmessage.filterMessages(params).list(sort:"dateReceived", order:"desc")
	
		def dates = [:]
		(params.startDate..params.endDate).each {
			dates[it.format('dd/MM')] = [sent :0, received : 0]
		}
				
		def stats = messages.collect {
			it.status == MessageStatus.INBOUND ? [date: it.dateReceived, type: "received"] : [date: it.dateSent, type: "sent"]
		}
		def messageGroups = countBy(stats.iterator(), {[it.date.format('dd/MM'), it.type]})
		messageGroups.each { key, value -> dates[key[0]][key[1]] += value }
		dates
	}
	
	//TODO: Remove in Groovy 1.8 (Needed for 1.7)
	private static def countAnswer(final Map<Object, Integer> answer, Object mappedKey) {
		if (!answer.containsKey(mappedKey)) {
			answer.put(mappedKey, 0)
		}
		int current = answer.get(mappedKey)
		answer.put(mappedKey, current + 1)
	}

	private static def Map<Object, Integer> countBy(Iterator self, Closure closure) {
		Map<Object, Integer> answer = new LinkedHashMap<Object, Integer>()
		while (self.hasNext()) {
			Object value = closure.call(self.next())
			countAnswer(answer, value)
		}
		answer
	}
	
	private static def toStartOfDay(Date d) {
		setTime(d, 0, 0, 0)
	}
	
	private static def toEndOfDay(Date d) {
		setTime(d, 23, 59, 59)
	}
	
	private static def setTime(Date d, int h, int m, int s) {
		def calc = Calendar.getInstance()
		calc.setTime(d)
		calc.set(Calendar.HOUR_OF_DAY, h)
		calc.set(Calendar.MINUTE, m)
		calc.set(Calendar.SECOND, s)
		calc.getTime()
	}
}
