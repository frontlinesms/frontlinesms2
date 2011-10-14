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
		dateReceived = dateReceived ?: new Date()
		dateSent = dateSent ?: new Date()
		if(status==MessageStatus.INBOUND? src: dst) updateContactName()
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
		contactName = fetchContactName(status == MessageStatus.INBOUND ? src : dst)
		contactExists = contactName && contactName != src && contactName != dst
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
		archived(nullable:true, validator: { val, obj ->
				if(val) {
					obj.messageOwner == null || obj.messageOwner instanceof RadioShow || (obj.messageOwner instanceof PollResponse && obj.messageOwner.poll.archived) ||	(obj.messageOwner instanceof Folder && obj.messageOwner.archived)
				} else {
					obj.messageOwner == null || obj.messageOwner instanceof RadioShow || (obj.messageOwner instanceof PollResponse && !obj.messageOwner.poll.archived) || (obj.messageOwner instanceof Folder && !obj.messageOwner.archived)
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
					if(getOnlyFailed)
						'in'("status", [MessageStatus.SEND_FAILED])
					else 
						'in'("status", [MessageStatus.SEND_PENDING, MessageStatus.SEND_FAILED])
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
							'ilike'("text", "%${search.searchString}%")
						}
						if(search.contactString) {
							'ilike'("contactName", "%${search.contactString}%")
						} 
						if(search.group) {
							def groupMembersNumbers = search.group.getAddresses()
							groupMembersNumbers = groupMembersNumbers?:[""] //otherwise hibernate fail to search 'in' empty list
							or {
								'in'("src",	groupMembersNumbers)
								'in'("dst", groupMembersNumbers)
							}
						}
						if(search.status) {
							'in'('status', search.status.tokenize(",")*.trim().collect { Enum.valueOf(MessageStatus.class, it)})
						}
						if(search.owners) {
							'in'("messageOwner", search.owners)
						}
						if(search.startDate && search.endDate) {
							between("dateReceived", search.startDate, search.endDate.next())
						} else if (search.startDate){	
							ge("dateReceived", search.startDate)
						} else if (search.endDate) {
							le("dateReceived", search.endDate.next())
						}
						if(search.customFields.find{it.value}) {
							def contactNameMatchingCustomField = CustomField.getAllContactNameMatchingCustomField(search.customFields)
							contactNameMatchingCustomField = contactNameMatchingCustomField?:[""] //otherwise hibernate fail to search 'in' empty list
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
				def startDate = params.startDate
				def endDate = params.endDate
				def groupMembers = groupInstance?.getAddresses() ?: ''
				and {
					if(groupInstance) {
						'in'("src",	 groupMembers)
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
							between("dateCreated", startDate, endDate)
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

	def toDelete() { // FIXME is this method necessary?
		this.deleted = true
		this
	}

	def addStar() { // FIXME is this method necessary?
		this.starred = true
		this
	}

	def removeStar() { // FIXME is this method necessary?
		this.starred = false
		this
	}
	
	def archive() {
		this.archived = true
		this
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
		Fmessage.findAllByStatus(MessageStatus.SEND_FAILED) ? true : false
	}
	
	static def getMessageOwners(activity) {
		activity instanceof Poll ? activity.responses : [activity]
	}

	static def getMessageStats(params) {
		def messages = Fmessage.filterMessages(params).list(sort:"dateReceived", order:"desc")
	
		def dates = [:]
		(params.startDate..params.endDate).each {
			dates[it.format('dd/MM')] = ["Sent" :0, "Received" : 0]
		}
		
		def stats = messages.collect {
			it.status == MessageStatus.INBOUND ? [date: it.dateReceived, type: "Received"] : [date: it.dateCreated, type: "Sent"]
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
}
