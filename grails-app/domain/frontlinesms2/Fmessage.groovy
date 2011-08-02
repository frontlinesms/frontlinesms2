package frontlinesms2

import frontlinesms2.enums.MessageStatus
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
		if(src) {
			Contact.withNewSession { session ->
				contactName = Contact.findByPrimaryMobile(src)?.name
			}
		}
	}
	
	static constraints = {
		src(nullable:true)
		dst(nullable:true)
		text(nullable:true)
		messageOwner(nullable:true)
		dateReceived(nullable:true)
		status(nullable:true)
		contactName(nullable:true)
	}
	
	static namedQueries = {
			inbox { isStarred ->
				and {
					eq("deleted", false)
					eq("archived", false)
					if(isStarred)
						eq("starred", true)
					eq("status", MessageStatus.INBOUND)
					isNull("messageOwner")
				}
			}
			sent { isStarred ->
				and {
					eq("deleted", false)
					eq("archived", false)
					eq("status", MessageStatus.SENT)
					isNull("messageOwner")
					if(isStarred)
						eq("starred", true)
				}
			}
			pending { isStarred ->
				and {
					eq("deleted", false)
					eq("archived", false)
					isNull("messageOwner")
					'in'("status", [MessageStatus.SEND_PENDING, MessageStatus.SEND_FAILED])
					if(isStarred)
						eq('starred', true)
				}
			}
			deleted { isStarred ->
				and {
					eq("deleted", true)
					eq("archived", false)
					if(isStarred)
						eq('starred', true)
				}
			}
			owned { isStarred, responses ->
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

			searchMessages {searchString, groupInstance, messageOwner -> 
				def groupMembers = groupInstance?.getAddresses()
				if(searchString)
					ilike("text", "%${searchString}%")
				and{
					if(groupInstance) {
						'in'("src",	 groupMembers)
					}
					if(messageOwner) {
						'in'("messageOwner", messageOwner)
					}
					eq('deleted', false)
				}
			}
			
			filterMessages { groupInstance, messageOwner, startDate, endDate -> 
				def groupMembers = groupInstance?.getAddresses()
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
	
	def getDisplayName(){
		contactName?:src
	}
	
	def toDelete() {
		this.deleted = true
		this
	}

	def addStar() {
		this.starred = true
		this
	}

	def removeStar() {
		this.starred = false
		this
	}
	
	def archive() {
		this.archived = true
		this
	}

	static def getFolderMessages(folderId) {
		def folder = Folder.get(folderId)
		def messages = Fmessage.owned(folder).list()
		messages
	}

	static def getInboxMessages(params) {
		def messages = Fmessage.inbox(params['starred']).list(params)
		messages
	}

	static def getSentMessages(params) {
		def messages = Fmessage.sent(params['starred']).list(params)
		messages
	}

	static def getPendingMessages(params) {
		def messages = Fmessage.pending(params['starred']).list(params)
		messages
	}

	static def getDeletedMessages(params) {
		def messages = Fmessage.deleted(params['starred']).list(params)
		messages
	}

	static def countInboxMessages(isStarred) {
		def messageCount = Fmessage.inbox(isStarred).count()
		messageCount
	}
	
	static def countSentMessages(isStarred) {
		def messageCount = Fmessage.sent(isStarred).count()
		messageCount
	}
	
	static def countPendingMessages(isStarred) {
		def messageCount = Fmessage.pending(isStarred).count()
		messageCount
	}
	
	static def countDeletedMessages(isStarred) {
		def messageCount = Fmessage.deleted(isStarred).count()
		messageCount
	}
	
	static def countUnreadMessages(isStarred) {
		def messageCount = Fmessage.unread().count()
		messageCount
	}
	
	static def countAllMessages(isStarred) {
		def inboxCount = Fmessage.countInboxMessages()
		def sentCount = Fmessage.countSentMessages()
		def pendingCount = Fmessage.countPendingMessages()
		def deletedCount = Fmessage.countDeletedMessages()
		[inbox: inboxCount, sent: sentCount, pending: pendingCount, deleted: deletedCount]
	}
	
	static def getMessageOwners(activity) {
		activity instanceof Poll ? activity.responses : [activity]
	}
	
	static def search(String searchString=null, Group groupInstance=null, Collection<MessageOwner> messageOwner=[], max, offset) {
		if(!searchString) return []
		def searchResults = Fmessage.searchMessages(searchString, groupInstance, messageOwner).list(sort:"dateReceived", order:"desc", max: max, offset: offset)
		searchResults
	}

	static def countAllSearchMessages(String searchString=null, Group groupInstance=null, Collection<MessageOwner> messageOwners=[]) {
		if(!searchString) return 0
		return Fmessage.searchMessages(searchString, groupInstance, messageOwners).count()
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
