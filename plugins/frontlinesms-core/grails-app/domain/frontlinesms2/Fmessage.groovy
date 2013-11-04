package frontlinesms2

import groovy.time.*

class Fmessage {
	static final int MAX_TEXT_LENGTH = 1600
	static final String TEST_MESSAGE_TEXT = "Test Message"

	static belongsTo = [messageOwner:MessageOwner]
	static transients = ['hasSent', 'hasPending', 'hasFailed', 'displayName' ,'outboundContactList', 'read', 'receivedOn']
	
	Date date = new Date() // No need for dateReceived since this will be the same as date for received messages and the Dispatch will have a dateSent
	Date dateCreated // This is unused and should be removed, but doing so throws an exception when running the app and I cannot determine why
	
	String src
	String text
	String outboundContactName
	String inboundContactName
	boolean rd
	boolean starred
	boolean archived
	boolean isDeleted
	
	boolean inbound

	static hasMany = [dispatches:Dispatch, details:MessageDetail]

	static mapping = {
		sort date:'desc'
		inboundContactName formula:'(SELECT c.name FROM contact c WHERE c.mobile=src)'
		outboundContactName formula:'(SELECT MAX(c.name) FROM contact c, dispatch d WHERE c.mobile=d.dst AND d.message_id=id)'
		version false

	}
	
	static constraints = {
		messageOwner nullable:true
		src(nullable:true, validator: { val, obj ->
				val || !obj.inbound
		})
		text maxSize:MAX_TEXT_LENGTH
		inboundContactName nullable:true
		outboundContactName nullable:true
		archived(nullable:true, validator: { val, obj ->
				obj.messageOwner == null || obj.messageOwner.archived == val
		})
		inbound(nullable:true, validator: { val, obj ->
				val ^ (obj.dispatches? true: false)
		})
		dispatches nullable:true
		details nullable:true
	}

	def beforeInsert = {
		if(!this.inbound) this.read = true
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
				if(!archived) {
					projections { dispatches { eq('status', DispatchStatus.SENT) } }
				} else {
					projections {
						dispatches {
							or {
								eq('status', DispatchStatus.SENT)
								eq('status', DispatchStatus.PENDING)
								eq('status', DispatchStatus.FAILED)
							}
						}
					}
				}
				if(getOnlyStarred)
					eq("starred", true)
			}
		}
		pending { getOnlyFailed=false ->
			and {
				eq("isDeleted", false)
				eq("archived", false)
				if(getOnlyFailed) {
					projections { dispatches { eq('status', DispatchStatus.FAILED) } }
				} else {
					projections {
						dispatches {
							or {
								eq('status', DispatchStatus.PENDING)
								eq('status', DispatchStatus.FAILED)
							}
						}
					}
				}
			}
			projections {
				distinct 'id'
				property 'date'
				property 'id'
			}
		}

		pendingAndNotFailed {
			and {
				eq("isDeleted", false)
				eq("archived", false)
				projections {
					dispatches {
						eq('status', DispatchStatus.PENDING)
					}
				}
			}
			projections {
				distinct 'id'
				property 'date'
				property 'id'
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
		owned { MessageOwner owner, boolean getOnlyStarred=false, getSent=null ->
			and {
				eq("isDeleted", false)
				eq("messageOwner", owner)
				if(getOnlyStarred)
					eq("starred", true)
				if(getSent != null)
					eq("inbound", getSent)
			}
		}
		unread { MessageOwner owner=null ->
			and {
				eq("isDeleted", false)
				eq("archived", false)
				eq("inbound", true)
				eq('rd', false)
				if(owner == null)
					isNull("messageOwner")
				else
					eq("messageOwner", owner)
			}
		}
		totalUnread {
			and {
				eq("isDeleted", false)
				eq("archived", false)
				eq("inbound", true)
				eq('rd', false)
			}
		}

		search { ids ->
			'in'('id', ids)
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

	def getDisplayName() {
		if(inbound) {
			if(inboundContactName) return inboundContactName
			else if(id) return src.toPrettyPhoneNumber()
			else return Contact.findByMobile(src)?.name?: src.toPrettyPhoneNumber()
		} else if(dispatches.size() == 1) {
			if(outboundContactName) return outboundContactName
			else {
				def dst = (dispatches as List)[0].dst
				if(id) return dst.toPrettyPhoneNumber()
				else return Contact.findByMobile(dst)?.name?: dst.toPrettyPhoneNumber()
			}
		} else {
			return Integer.toString(dispatches.size())
		}
	}

	def getHasSent() { areAnyDispatches(DispatchStatus.SENT) }
	def getHasFailed() { areAnyDispatches(DispatchStatus.FAILED) }
	def getHasPending() { areAnyDispatches(DispatchStatus.PENDING) }
	def getOutboundContactList() {
		dispatches.collect { Contact.findByMobile(it.dst)?.name } - null
	}

	private boolean isMoveAllowed() {
		if(this.messageOwner){
			return !(this.messageOwner?.archived)
		} else {
			return (!this.isDeleted && !this.archived)
		}
	}

	private def areAnyDispatches(status) {
		dispatches?.any { it.status == status }
	}

	public void setText(String text) {
		this.text = text?.truncate(MAX_TEXT_LENGTH)
	}

	public boolean isRead() { return this.rd }
	public boolean setRead(boolean read) { this.rd = read }

	static def listPending(onlyFailed, params=[:]) {
		def ids = pending(onlyFailed).list(params) as List
		(!ids) ? [] : Fmessage.getAll(ids)
	}

	static def countPending(onlyFailed) {
		pending(onlyFailed).list().size()
	}

	static def hasFailedMessages() {
		return pending(true).count() > 0
	}
	
	static def countUnreadMessages() {
		Fmessage.unread.count()
	}

	static def countUnreadMessages(owner) {
		Fmessage.unread(owner).count()
	}

	static def countTotalUnreadMessages() {
		Fmessage.totalUnread.count()
	}
	
	static def countAllMessages() {
		['inbox', 'sent', 'pending', 'deleted'].collectEntries { [it, Fmessage[it].count()] }
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
	
	def updateDispatches() {
		if(isDeleted) {
			dispatches.each {
				it.isDeleted = true
			}
		}
	}

	def getReceivedOn() {
		Fconnection.findByMessages(this).list()[0]
	}

	def getMessageDetailValue(owner) {
		def ownerType = getOwnerType(owner)
		if(owner && (Activity.get(owner?.id) instanceof CustomActivity)) {
			def stepId = this.details.find { it.ownerType == ownerType && it.ownerId == owner.id }?.value
			def t = this.details.find { (it.ownerType == MessageDetail.OwnerType.STEP) && (it.ownerId == stepId as Long) }?.value
			return t
		} else {
			return this.details?.find { it.ownerType == ownerType && it.ownerId == owner?.id }?.value?:''
		}
	}

	//> GETTER AND SETTER OF MESSAGE DETAIL THAT USE CURRENT MESSAGE OWNER
	def getOwnerDetail() {
		getMessageDetailValue(this.messageOwner)
	}

	def setMessageDetail(activityOrStep, val) {
		if (activityOrStep instanceof Activity) {
			this.setMessageDetailValue(activityOrStep, val)
		} else {
			this.setMessageDetailValue(this.messageOwner, activityOrStep.id)
			this.setMessageDetailValue(activityOrStep, val)
		}
	}

	private def setMessageDetailValue(owner, value) {
		def ownerType = getOwnerType(owner)
		def messageDetailInstance = this.details.find { it.ownerType == ownerType && it.ownerId == owner.id }
		if(!messageDetailInstance) {
			messageDetailInstance = new MessageDetail(ownerType:ownerType, ownerId:owner.id)
			this.addToDetails(messageDetailInstance)
		}
		messageDetailInstance.value = value
		this.save(failOnError:true)
		messageDetailInstance.save(failOnError:true)
	}

	private def getOwnerType(owner) {
		owner instanceof Step ? MessageDetail.OwnerType.STEP : MessageDetail.OwnerType.ACTIVITY
	}

	def clearAllDetails() {
		this.details?.clear()
	}

}

