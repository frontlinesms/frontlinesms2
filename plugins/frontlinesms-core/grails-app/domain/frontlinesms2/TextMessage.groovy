package frontlinesms2

import groovy.time.*

class TextMessage extends Interaction {
	def mobileNumberUtilService

	static final int MAX_TEXT_LENGTH = 1600
	static final String TEST_MESSAGE_TEXT = "Test Message"
	
	String text
	
	boolean inbound

	static hasMany = [dispatches:Dispatch, details:MessageDetail]

	static constraints = {
		text maxSize:MAX_TEXT_LENGTH
		inboundContactName nullable:true
		outboundContactName nullable:true
		inbound(nullable:true, validator: { val, obj ->
				val ^ (obj.dispatches? true: false)
		})
		dispatches nullable:true
		details nullable:true
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
	} << Interaction.namedQueries // Named Queries are not inherited

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

	def getContactFlagCSSClasses() {
		def flagCssClass
		if(inbound) {
			flagCssClass = mobileNumberUtilService.getFlagCSSClasses(src)
		} else if(dispatches.size() == 1) {
			def dst = (dispatches as List)[0].dst
			flagCssClass = mobileNumberUtilService.getFlagCSSClasses(dst)
		}
		flagCssClass
	}

	def getHasSent() { areAnyDispatches(DispatchStatus.SENT) }
	def getHasFailed() { areAnyDispatches(DispatchStatus.FAILED) }
	def getHasPending() { areAnyDispatches(DispatchStatus.PENDING) }
	def getOutboundContactList() {
		dispatches.collect { Contact.findByMobile(it.dst)?.name } - null
	}

	private def areAnyDispatches(status) {
		dispatches?.any { it.status == status }
	}

	public void setText(String text) {
		this.text = text?.truncate(MAX_TEXT_LENGTH)
	}

	static def listPending(onlyFailed, params=[:]) {
		def ids = pending(onlyFailed).list(params) as List
		(!ids) ? [] : TextMessage.getAll(ids)
	}
	static def countUnreadMessages() {
		TextMessage.unread.count()
	}

	static def countPending(onlyFailed) {
		pending(onlyFailed).list().size()
	}

	static def hasFailedMessages() {
		return pending(true).count() > 0
	}
	
	static def countAllMessages() {
		['inbox', 'sent', 'pending', 'deleted'].collectEntries { [it, TextMessage[it].count()] }
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
			TextMessage.forReceivedStats(params).list().each { m ->
				++dates[asKey(m.date)].received
			}
		}
		
		dates
	}
	
	def updateDispatches() {
		if(isDeleted) {
			dispatches.each {
				it.isDeleted = true
			}
		}
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

	def clearAllDetails() {
		this.details?.clear()
	}

}

