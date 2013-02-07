package frontlinesms2

class FmessageService {
	def messageSendService
	
    def move(messageList, activity, params) {
    	def messagesToSend = []
    	messageList.each { messageInstance ->	
    		if(messageInstance.isMoveAllowed()) {	
    			messageInstance.ownerDetail = null
    			messageInstance.isDeleted = false
				Trash.findByObject(messageInstance)?.delete(failOnError:true)
				if (params.messageSection == 'activity') {
					activity.move(messageInstance)
					activity.save(failOnError:true, flush:true)
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
