package frontlinesms2

class PollController {

	def messageSendService
	static allowedMethods = [update: "POST"]

	def index = {
		[polls: Poll.findAllByArchivedAndDeleted(params.viewingArchive, false),
				messageSection: "poll"]
	}

	def rename = {
	}

	def create = {
		[contactList: Contact.list(),
			groupList:Group.getGroupDetails()]
	}

	def save = {
		if(!params.enableKeyword) params.keyword = null
		
		def pollInstance = Poll.createPoll(params)
		if(!params.dontSendMessage) {
			def messages = messageSendService.getMessagesToSend(params)
			messages.each { message ->
				pollInstance.addToMessages(message)
				pollInstance.save()
				messageSendService.send(message)
			}
			flash.message = "Poll has been saved and message(s) has been queued to send"
		} else {
			pollInstance.save()
			flash.message = "Poll has been saved"
		}
		pollInstance.save(flush: true)
		[ownerId: pollInstance.id]
	}
	
	def update = {
		def poll = Poll.get(params.id)
		poll.properties = params
		poll.save()
		redirect(controller: "message", action: "poll", params: [ownerId: params.id])
	}

	def archive = {
		def poll = Poll.get(params.id)
		poll.archivePoll()
		poll.save()
		flash.message = "Poll archived successfully!"
		redirect(controller: "message", action: "inbox")
	}
	
	def unarchive = {
		def poll = Poll.get(params.id)
		poll.unarchivePoll()
		poll.save()
		flash.message = "Poll unarchived successfully!"
		redirect(controller: "archive", action: "pollList", params:[viewingArchive: true])
	}
	
	def confirmDelete = {
		def pollInstance = Poll.get(params.id)
		render view: "../message/confirmDelete", model: [ownerInstance: pollInstance]
	}
	
	def delete = {
		def poll = Poll.get(params.id)
		poll.deleted = true
		new Trash(identifier:poll.title, message:"${poll.liveMessageCount}", objectType:poll.class.name, linkId:poll.id).save(failOnError: true, flush: true)
		poll.save(failOnError: true, flush: true)
		flash.message = "Poll has been trashed!"
		redirect(controller:"message", action:"inbox")
	}
	
	def restore = {
		def poll = Poll.get(params.id)
		poll.deleted = false
		Trash.findByLinkId(poll.id)?.delete()
		poll.save(failOnError: true, flush: true)
		flash.message = "Poll has been restored!"
		redirect(controller: "message", action: "trash")
	}
	
	def create_new_activity = {
	}
}
