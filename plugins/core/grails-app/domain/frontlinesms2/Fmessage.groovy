package frontlinesms2

import groovy.time.*
import org.hibernate.FlushMode

class Fmessage {
	static belongsTo = [messageOwner:MessageOwner]
	
	Date date = new Date() // No need for dateReceived since this will be the same as date for received messages and the Dispatch will have a dateSent
	Date dateCreated // This is unused and should be removed, but doing so throws an exception when running the app and I cannot determine why
	
	String src
	String text
	String displayName
	boolean contactExists
	
	boolean read
	boolean starred
	boolean archived
	boolean isDeleted
	
	boolean inbound
	boolean hasSent
	boolean hasPending
	boolean hasFailed
	int failedCount
	static hasMany = [dispatches:Dispatch]

	static mapping = { sort date:'desc' }
	
	static constraints = {
		messageOwner(nullable:true)
		date(nullable:false)
		src(nullable:true, validator: { val, obj ->
				if(!val)
					!obj.inbound
		})
		text(nullable:true)
		displayName(nullable:true)
		contactExists(nullable:true)
		archived(nullable:true, validator: { val, obj ->
				if(val) {
					obj.messageOwner == null || obj.messageOwner.archived
				} else {
					obj.messageOwner == null ||  !obj.messageOwner.archived
				}
		})
		inbound(nullable: true, validator: { val, obj ->
				if(val) {
					obj.hasSent == null || obj.hasSent == false
					obj.hasPending == null || obj.hasPending == false
					obj.hasFailed == null || obj.hasFailed == false
					obj.dispatches == null || obj.dispatches.size() == 0
				} else {
					obj.dispatches != null && obj.dispatches?.size() >= 1
				}
		})
		hasSent(nullable: true, validator: { val, obj ->
			if(val)
				!obj.inbound
		})
		hasPending(nullable: true, validator: { val, obj ->
			if(val)
				!obj.inbound
		})
		hasFailed(nullable: true, validator: { val, obj ->
			if(val)
				!obj.inbound
		})
		dispatches(nullable: true)
	}

	def beforeInsert = {
		updateFmessageStatuses()
		withSession { session -> 
			FlushMode flushMode = session.flushMode 
			session.flushMode = FlushMode.MANUAL 
			try { 
				updateFmessageDisplayName()
			} finally {
				session.flushMode = flushMode
			}
		}
		if(!this.inbound) this.read = true
	}
	
	def beforeUpdate = {
		updateFmessageStatuses()
	}
	
	static namedQueries = {
		inbox { getOnlyStarred=false, archived=false ->
			and {
				eq("isDeleted", false)
				eq("archived", archived)
				if(getOnlyStarred)
					eq("starred", true)
				eq("inbound", true)
				isNull("messageOwner")
			}
		}
		sent { getOnlyStarred=false, archived=false ->
			and {
				eq("isDeleted", false)
				eq("archived", archived)
				eq("hasSent", true)
				if(getOnlyStarred)
					eq("starred", true)
			}
		}
		pending { getOnlyFailed=false ->
			and {
				eq("isDeleted", false)
				eq("archived", false)
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
				eq("isDeleted", true)
				eq("archived", false)
				if(getOnlyStarred)
					eq('starred', true)
			}
		}
		owned { MessageOwner owner, boolean getOnlyStarred=false, boolean getSent=false ->
			and {
				eq("isDeleted", false)
				eq("messageOwner", owner)
				if(getOnlyStarred)
					eq("starred", true)
				if(!getSent)
					eq("inbound", true)
			}
		}
		unread {
			and {
				eq("isDeleted", false)
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
					def groupMembersNumbers = search.group.getAddresses() ?: [''] //otherwise hibernate fail to search 'in' empty list
					or {
						'in'("src", groupMembersNumbers)
						dispatches {
							'in'("dst", groupMembersNumbers)
						}
					}
				}
				if(search.status) {
					if(search.status.toLowerCase() == 'inbound') eq('inbound', true)
					else eq('inbound', false)
				}
				if(search.owners) {
					'in'("messageOwner", search.owners)
				}
				if(search.startDate && search.endDate) {
					between("date", search.startDate, search.endDate)
				} else if (search.startDate) {	
					ge("date", search.startDate)
				} else if (search.endDate) {
					le("date", search.endDate)
				}
				if(search.customFields.any { it.value }) {
					def matchingContactsNumbers = CustomField.getAllContactsWithCustomField(search.customFields).primaryMobile ?: [""] //otherwise hibernate fails to search 'in' empty list
					or {
						'in'("src", matchingContactsNumbers)
						dispatches {
							'in'("dst", matchingContactsNumbers)
						}
					}
				}
				if(!search.inArchive) {
					eq('archived', false)
				}
				eq('isDeleted', false)
			}
		}
		
		forReceivedStats { params ->
			def groupInstance = params.groupInstance
			def messageOwner = params.messageOwner
			def startDate = params.startDate.startOfDay
			def endDate = params.endDate.endOfDay
			
			and {
				eq('inbound', true)
				eq('isDeleted', false)
				between("date", startDate, endDate)
				if(groupInstance) 'in'('src', groupInstance?.addresses ?: "")
				if(messageOwner) 'in'('messageOwner', messageOwner)
			}
		}
	}

	def getDisplayText() {
		def p = PollResponse.withCriteria {
			messages {
				eq('isDeleted', false)
				eq('archived', false)
				eq('id', this.id)
			}
		}

		p?.size() ? "${p[0].value} (\"${this.text}\")" : this.text
	}
	
	static def countUnreadMessages() {
		Fmessage.unread.count()
	}
	
	static def countAllMessages(params) {
		def inboxCount = Fmessage.inbox.count()
		def sentCount = Fmessage.sent.count()
		def pendingCount = Fmessage.pending.count()
		def deletedCount = Fmessage.deleted.count()
		[inbox: inboxCount, sent: sentCount, pending: pendingCount, deleted: deletedCount]
	}

	static def hasFailedMessages() {
		return Fmessage.countByHasFailedAndIsDeleted(true, false) > 0
	}
	
	// TODO should this be in a service?
	static def getMessageStats(params) {
		def asKey = { date -> date.format('dd/MM') }
		
		def dates = [:]
		(params.startDate..params.endDate).each { date ->
			dates[asKey(date)] = [sent:0, received:0]
		}
		
		if(!params.inbound) {
			// TODO the named query should ideally do the counts for us
			Dispatch.forSentStats(params).list().each { d ->
				++dates[asKey(d.dateSent)].sent
			}
		}
		
		if(params.inbound == null || params.inbound) {
			// TODO the named query should ideally do the counts for us
			Fmessage.forReceivedStats(params).list().each { m ->
				++dates[asKey(m.date)].received
			}
		}
		
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
	
	private def updateFmessageDisplayName() {
		Contact c
		if(inbound) {
			if(src &&
					(c = Contact.findByPrimaryMobile(src))) {
				displayName = c.name
				contactExists = true
			} else {
				displayName = src
				contactExists = false
			}
		} else {
			if(dispatches?.size() == 1) {
				def dst = dispatches.dst[0]
				if((c = Contact.findByPrimaryMobile(dst))) {
					displayName = "To: " + c.name
					contactExists = true
				} else {
					displayName = "To: " + dst
					contactExists = false
				}
			} else if(dispatches?.size() > 1) {
				displayName = "To: " + dispatches?.size() + " recipients"
				contactExists = true
			}
		}
	}
	
	def updateFmessageStatuses() {
		if(!inbound) {
			hasFailed = dispatches.any { it.status == DispatchStatus.FAILED }
			hasPending = dispatches.any { it.status == DispatchStatus.PENDING }
			hasSent = dispatches.any { it.status == DispatchStatus.SENT }
		}
	}
	
	def updateDispatches() {
		if(isDeleted) {
			dispatches.each {
				it.isDeleted = true
			}
		}
	}
}
