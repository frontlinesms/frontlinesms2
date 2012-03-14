package frontlinesms2

import java.util.Date

class Dispatch {
	static belongsTo = [message: Fmessage]
	String dst
	DispatchStatus status
	Date dateSent
	
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
	
	def beforeInsert = {
		updateMessageStatus()
	}
	
	def beforeUpdate = {
		updateMessageStatus()
	}
	
	def afterUpdate = {
		Fmessage.withNewSession { session ->
				println "Dispatch.afterUpdate() : inside new session..."
				def variables = [true, false, message.id]
				if(status == DispatchStatus.FAILED) {
					Fmessage.executeUpdate("UPDATE Fmessage m SET m.hasFailed=?,m.hasPending=? WHERE m.id=?" , variables)
				}
				if(status == DispatchStatus.SENT) {
					Fmessage.executeUpdate("UPDATE Fmessage m SET m.hasSent=?,m.hasPending=? WHERE m.id=?" , variables)
				}
			}
		println "Dispatch.afterUpdate() : EXIT"
	}
	
	def updateMessageStatus() {
		message.updateFmessageStatuses()
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
				if(groupInstance) 'in'('dst', groupInstance.addresses)
				message {
					if(messageOwner) 'in'('messageOwner', messageOwner)
				}
				
			}
		}
		
		messageCount { contact ->
			and {
				eq('isDeleted', false)
				if(contact.primaryMobile) 'in'('dst', contact.primaryMobile)
				if(contact.secondaryMobile) 'in'('dst', contact.secondaryMobile)
			}
		}
	}
}
