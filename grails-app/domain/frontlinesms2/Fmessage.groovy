package frontlinesms2

import groovy.time.*


class Fmessage {
	String src
	String dst
	String text
	String displayName
	Date dateCreated
	Date dateReceived
	Date dateSent
	boolean inbound
	boolean contactExists
	boolean read
	boolean deleted
	boolean starred
	boolean archived
	
	boolean hasSent
	boolean hasPending
	boolean hasFailed
	
	static belongsTo = [messageOwner:MessageOwner]
	static hasMany = [dispatches:Dispatch]

	static mapping = {
		sort dateReceived:'desc'
		sort dateSent:'desc'
	}

	def beforeInsert = {
		if(inbound) {
			dateReceived = dateReceived ?: new Date()
		} else {
			dateSent = dateSent ?: new Date()
		}
		updateFmessageDisplayName()
	}
	
	private String findContact(String number) {
		return Contact.findByPrimaryMobile(number)?.name ?: (Contact.findBySecondaryMobile(number)?.name ?: number)
	}
	
	static constraints = {
		src(nullable:true)
		dst(nullable:true)
		text(nullable:true)
		messageOwner(nullable:true)
		dateReceived(nullable:true)
		dateSent(nullable:true)
		displayName(nullable:true)
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
		inbox { getOnlyStarred=false, archived=false ->
			and {
				eq("deleted", false)
				eq("archived", archived)
				if(getOnlyStarred)
					eq("starred", true)
				eq("inbound", true)
				isNull("messageOwner")
			}
		}
		sent { getOnlyStarred=false, archived=false ->
			and {
				eq("deleted", false)
				eq("archived", archived)
				eq("hasSent", true)
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
				if(getOnlyFailed) {
					eq("hasFailed", true)
				} else {
					or {
						eq("hasPending", true)	
						eq("hasFailed", true)
					}
				}
					
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
				eq("inbound", true)
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
					ilike("displayName", "%${search.contactString}%")
				} 
				if(search.group) {
					def groupMembersNumbers = search.group.getAddresses()?:[''] //otherwise hibernate fail to search 'in' empty list
					or {
						'in'("src", groupMembersNumbers)
						'in'("dst", groupMembersNumbers)
					}
				}
				if(search.status) {
					def statuses = search.status.tokenize(',').collect { it.trim().toLowerCase() }
					or {
						if('sent' in statuses) eq('hasSent', true)
						if('pending' in statuses) eq('hasPending', true)
						if('failed' in statuses) eq('hasFailed', true)
						if('inbound' in statuses) eq('inbound', true)
					}
				}
				if(search.owners) {
					'in'("messageOwner", search.owners)
				}
				if(search.startDate && search.endDate) {
					between("dateReceived", search.startDate, search.endDate)
				} else if (search.startDate) {	
					ge("dateReceived", search.startDate)
				} else if (search.endDate) {
					le("dateReceived", search.endDate)
				}
				if(search.customFields.any { it.value }) {
					def matchingContacts = CustomField.getAllContactsWithCustomField(search.customFields) ?: [""] //otherwise hibernate fails to search 'in' empty list
					'in'("displayName", matchingContacts)
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
					def statuses = params.messageStatus.collect { it.toLowerCase() }
					or {
						if('sent' in statuses) eq('hasSent', true)
						if('pending' in statuses) eq('hasPending', true)
						if('failed' in statuses) eq('hasFailed', true)
						if('inbound' in statuses) eq('inbound', true)
					}
				}
				eq('deleted', false)
				or {
					and {
						between("dateReceived", startDate, endDate)
						eq("inbound", true)
					}
					and {
						between("dateSent", startDate, endDate)
						eq("hasSent", true)
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
		Fmessage.countByHasFailed(true) > 0
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
			it.inbound ? [date: it.dateReceived, type: "received"] : [date: it.dateSent, type: "sent"]
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
	
	private def updateFmessageDisplayName() {
		if(inbound && Contact.findByPrimaryMobile(src)) {
			displayName = Contact.findByPrimaryMobile(src).name
			contactExists = true
		} else if(!inbound && dispatches?.count() == 1 && Contact.findByPrimaryMobile(dispatches*.dst.flatten())) {
			displayName = Contact.findByPrimaryMobile(dispatches*.dst.flatten())
			contactExists = true
		}
	}
}
