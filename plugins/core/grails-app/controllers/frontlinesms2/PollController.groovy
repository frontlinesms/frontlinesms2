package frontlinesms2

class PollController extends ActivityController {

	def index = {  //FIXME is this doing anything? its referenced in some tests, but i dont think the app ever uses it
		[polls: Poll.findAllByArchivedAndDeleted(params.viewingArchive, false),
				messageSection: "activity"]
	}

	def save = {
		if(!params.enableKeyword) params.keyword = null
		
		def pollInstance = Poll.createPoll(params)
		pollInstance.sentMessageText = params.messageText
		if(!params.dontSendMessage) {
			def message = messageSendService.getMessagesToSend(params)
			pollInstance.addToMessages(message)
			messageSendService.send(message)
			pollInstance.save()
			flash.message = "Poll has been saved and message(s) has been queued to send"
		} else {
			pollInstance.save()
			flash.message = "Poll has been saved"
		}
		[ownerId: pollInstance.id]
	}
}
