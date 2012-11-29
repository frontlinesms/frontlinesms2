package frontlinesms2

class FmessageService {
	def messageSendService
	
    def move(messageList, activity, params) {
    	def messagesToSend = []
    	messageList.each { messageInstance ->
    		if(messageInstance.isMoveAllowed()){
    			messageInstance.clearAllDetails()
    			messageInstance.isDeleted = false
				Trash.findByObject(messageInstance)?.delete(failOnError:true)
				if (params.messageSection == 'activity') {
					messageInstance.messageOwner?.removeFromMessages(messageInstance)?.save(failOnError:true)
					activity.addToMessages(messageInstance)
					if(activity.metaClass.hasProperty(null, 'autoreplyText') && activity.autoreplyText) {
						params.addresses = messageInstance.src
						params.messageText = activity.autoreplyText
						def outgoingMessage = messageSendService.createOutgoingMessage(params)
						outgoingMessage.save()
						messagesToSend << outgoingMessage
						activity.addToMessages(outgoingMessage)
						activity.save()
					} else if(activity instanceof Webconnection) {
						activity.processKeyword(messageInstance, null)
					}else if(activity instanceof Autoforward) {
						activity.processKeyword(messageInstance, null)
					}
				} else if (params.ownerId && params.ownerId != 'inbox') {
					messageInstance.messageOwner?.removeFromMessages(messageInstance)?.save(failOnError:true)
					MessageOwner.get(params.ownerId).addToMessages(messageInstance).save(failOnError:true)
					messageInstance.save()
				} else {
					messageInstance.with {
						if(messageOwner) {
							messageOwner.removeFromMessages(messageInstance).save(failOnError:true)
							save(failOnError:true)
						}
					}
				}
    		}
		}
		if(messagesToSend) {
			MessageSendJob.defer(messagesToSend)
		}
    }
}
