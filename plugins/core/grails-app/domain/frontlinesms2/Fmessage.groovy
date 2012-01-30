package frontlinesms2

import groovy.time.*
import java.util.Date
import org.hibernate.FlushMode


class Fmessage {
	static belongsTo = [messageOwner:MessageOwner]
	
	Date date	// No need for dateReceived since this will be the same as date for received messages and the Dispatch will have a dateSent
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

	static mapping = {
		sort date:'desc'
	}
	
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
		withSession { session -> 
	       FlushMode flushMode = session.flushMode 
	       session.flushMode = FlushMode.MANUAL 
	       try { 
			   updateFmessageDisplayName()
	       } finally {
		   		session.flushMode = flushMode
	       }
	   }
		updateFmessageStatuses()
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
		owned { getOnlyStarred=false, owners, getSent=false ->
			and {
				eq("isDeleted", false)
				'in'("messageOwner", owners)
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
						if('sent' in statuses) {
							or {
								eq('hasSent', true)
								eq('hasPending', true)
								eq('hasFailed', true)
							}
						}
						if('inbound' in statuses) eq('inbound', true)
					}
				}
				eq('isDeleted', false)
				and {
					between("date", startDate, endDate)
					eq("inbound", true)
				}
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
		if(Fmessage.findByHasFailedAndIsDeleted(true, false))
			return true
		return false
	}
	
	// TODO should this be in a service?
	static def getMessageStats(params) {
		def messages = Fmessage.filterMessages(params).list(sort:"date", order:"desc")
	
		def dates = [:]
		(params.startDate..params.endDate).each {
			dates[it.format('dd/MM')] = [sent : 0, received : 0]
		}
				
		def stats = messages.collect {
			it.inbound ? [date: it.date, type: "received"] : [date: it.date, type: "sent"]
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
		} else if(inbound) {
			displayName = src
			contactExists = false
		} else if(!inbound && dispatches?.size() == 1 && Contact.findByPrimaryMobile(dispatches.dst[0])) {
			displayName = "To: " + Contact.findByPrimaryMobile(dispatches.dst[0]).name
			contactExists = true
		} else if(!inbound && dispatches?.size() == 1) {
			displayName = "To: " + dispatches.dst[0]
			contactExists = false
		} else if(!inbound && dispatches?.size() > 1) {
			displayName = "To: " + dispatches?.size() + " recipients"
			contactExists = true
		}
	}
	
	def updateFmessageStatuses() {
		if(!this.inbound) {
			this.failedCount = 0
			this.hasFailed = false
			this.hasPending = false
			this.hasSent = false
			this.dispatches.each {
				if(it.status == DispatchStatus.FAILED) {
					this.hasFailed = true
					this.failedCount++
				} else if(it.status == DispatchStatus.PENDING) {
					this.hasPending = true
				} else if(it.status == DispatchStatus.SENT) {
					this.hasSent = true
				}
			}
		}
	}
	
	def updateDispatches() {
		if(this.isDeleted)
			this.dispatches.each {
				it.isDeleted = true
			}
	}
}
