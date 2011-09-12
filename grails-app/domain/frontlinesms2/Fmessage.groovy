package frontlinesms2

import groovy.time.*

class Fmessage {
	String src
	String dst
	String text
	String contactName
	Date dateCreated
	Date dateReceived
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
		autoTimestamp false
	}

	def beforeInsert = {
		dateCreated = dateCreated ? dateCreated : new Date()
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
		contactName = fetchContactName(status==MessageStatus.INBOUND? src: dst)
	}
	
	static constraints = {
		src(nullable:true)
		dst(nullable:true)
		text(nullable:true)
		messageOwner(nullable:true)
		dateReceived(nullable:true)
		status(nullable:true)
		contactName(nullable:true)
		archived(nullable:true, validator: { val, obj ->
				if(val) {
					obj.messageOwner == null || obj.messageOwner.poll?.archived == true				
				}
		})
	}
	
	static namedQueries = {
			inbox { isStarred=false, archived=false ->
				and {
					eq("deleted", false)
					eq("archived", archived)
					if(isStarred)
						eq("starred", true)
					eq("status", MessageStatus.INBOUND)
					isNull("messageOwner")
				}
			}
			sent { isStarred=false, archived=false ->
				and {
					eq("deleted", false)
					eq("archived", archived)
					eq("status", MessageStatus.SENT)
					isNull("messageOwner")
					if(isStarred)
						eq("starred", true)
				}
			}
			pending { hasFailed=false ->
				and {
					eq("deleted", false)
					eq("archived", false)
					isNull("messageOwner")
					if(hasFailed)
						'in'("status", [MessageStatus.SEND_FAILED])
					else 
						'in'("status", [MessageStatus.SEND_PENDING, MessageStatus.SEND_FAILED])
				}
			}
			deleted { isStarred=false ->
				and {
					eq("deleted", true)
					eq("archived", false)
					if(isStarred)
						eq('starred', true)
				}
			}
			owned { isStarred=false, responses ->
				and {
					eq("deleted", false)
					eq("archived", false)
					'in'("messageOwner", responses)
					if(isStarred)
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
				def groupMembersNumbers = search.group?.getAddresses()
					and {
						if(search.searchString) {
							'ilike'("text", "%${search.searchString}%")
						}
						if(search.contactString) {
							'ilike'("contactName", "%${search.contactString}%")
						}
						if(groupMembersNumbers) {
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
						if(!search.inArchive) {
							eq('archived', false)
						}
						eq('deleted', false)
					}
			}
			
			filterMessages { groupInstance, messageOwner, startDate, endDate -> 
				def groupMembers = groupInstance?.getAddresses() ?: ''
				and {
					if(groupInstance) {
						'in'("src",	 groupMembers)
					}
					if(messageOwner) {
						'in'("messageOwner", messageOwner)
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

	static def getFolderMessages(folderId) {
		def folder = Folder.get(folderId) // TODO check if we need to fetch the folder here rather than just pass the ID
		Fmessage.owned(folder).list()
	}

	static def getInboxMessages(params) {
		Fmessage.inbox(params['starred'], params["archived"]).list(params)
	}

	static def getSentMessages(params) {
		Fmessage.sent(params['starred'],  params["archived"]).list(params)
	}

	static def getPendingMessages(params) {
		Fmessage.pending(params['failed']).list(params)		
	}

	static def getDeletedMessages(params) {
		Fmessage.deleted(params['starred']).list(params)
	}

	static def countInboxMessages(params) {
		Fmessage.inbox(params['starred'], params['archived']).count()
	}
	
	static def countSentMessages(params) {
		Fmessage.sent(params['starred'], params['archived']).count()
	}
	
	static def countPendingMessages(isStarred) {
		Fmessage.pending(isStarred).count()
	}
	
	static def countDeletedMessages(isStarred) {
		Fmessage.deleted(isStarred).count()
	}
	
	static def countUnreadMessages(isStarred) {
		Fmessage.unread().count()
	}
	
	static def countAllMessages(params) {
		def inboxCount = Fmessage.countInboxMessages(params)
		def sentCount = Fmessage.countSentMessages(params)
		def pendingCount = Fmessage.countPendingMessages()
		def deletedCount = Fmessage.countDeletedMessages()
		[inbox: inboxCount, sent: sentCount, pending: pendingCount, deleted: deletedCount]
	}

	static def hasUndeliveredMessages() {
		Fmessage.getPendingMessages([:]).any {it.status == MessageStatus.SEND_FAILED}
	}
	
	static def getMessageOwners(activity) {
		activity instanceof Poll ? activity.responses : [activity]
	}

	static def getMessageStats( Group groupInstance=null, Collection<MessageOwner> messageOwner=[], Date startDate = new Date(Long.MIN_VALUE), Date endDate = new Date(Long.MAX_VALUE)) {
		def messages = Fmessage.filterMessages(groupInstance, messageOwner, startDate, endDate).list(sort:"dateReceived", order:"desc")
	
		def dates = [:]
		(startDate..endDate).each {
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
