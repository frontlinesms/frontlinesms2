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
			flash.message = "Poll has been saved and message(s) has been queued to send to " + messages*.dst.join(", ")
		} else {
			pollInstance.save()
			flash.message = "Poll has been saved"
		}
		pollInstance.save(flush: true)
		redirect(controller: "message", action: "pending", params:params)
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
		flash.message = "Poll was archived successfully!"
		redirect(controller: "message", action: "inbox")
	}
	
	def unarchive = {
		def poll = Poll.get(params.id)
		poll.unarchivePoll()
		poll.save()
		flash.message = "Poll was unarchived successfully!"
		redirect(controller: "archive", action: "pollView")
	}
	
	def confirmDelete = {
		def pollInstance = Poll.get(params.id)
		render view: "../message/confirmDelete", model: [ownerInstance: pollInstance]
	}
	
	def delete = {
		def poll = Poll.get(params.id)
		poll.toDelete()
		new Trash(identifier:poll.title, message:"${poll.liveMessageCount}", objectType:poll.class.name, linkId:poll.id).save(failOnError: true, flush: true)
		poll.save(failOnError: true, flush: true)
		flash.message = "Poll has been trashed!"
		redirect(controller:"message", action:"inbox")
	}
	
	def create_new_activity = {
	}
}
