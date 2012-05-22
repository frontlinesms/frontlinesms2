package frontlinesms2

class Dispatch {
	static belongsTo = [message: Fmessage]
	String dst
	DispatchStatus status
	Date dateSent
	def expressionProcessorService
	
	boolean isDeleted
	
	static constraints = {
		dst(nullable:false)
		status(nullable: false, validator: { val, obj ->
				if(val == DispatchStatus.SENT)
					obj.dateSent != null
				else
					obj.dateSent == null
		})
		dateSent(nullable: true, validator: { val, obj ->
				if(val)
					obj.status == DispatchStatus.SENT
				else
					obj.status != DispatchStatus.SENT
		})
		isDeleted(nullable: true, validator: { val, obj ->
				if(val)
					obj.message.isDeleted
		})
	}

	String getText() {
		return expressionProcessorService.process(this)
	}
	
	static namedQueries = {
		forSentStats { params ->
			def groupInstance = params.groupInstance
			def messageOwner = params.messageOwner
			def startDate = params.startDate.startOfDay
			def endDate = params.endDate.endOfDay
			def statuses = params.messageStatus.collect { it.toLowerCase() }
			
			and {
				eq('isDeleted', false)
				between("dateSent", startDate, endDate)
				if(groupInstance) 'in'('dst', groupInstance?.addresses ?: "")
				message {
					if(messageOwner) 'in'('messageOwner', messageOwner)
				}
				
			}
		}
		
		messageCount { contact ->
			and {
				eq('isDeleted', false)
				if(contact.mobile) 'in'('dst', contact.mobile)
			}
		}
	}
}
