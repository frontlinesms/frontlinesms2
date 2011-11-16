package frontlinesms2

import groovy.lang.Closure;

class AnnouncementController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	def messageSendService
	
	def index = {
		redirect(action:'create')
	}
	
	def create = {
		[contactList: Contact.list(),
			groupList: Group.getGroupDetails()]
	}

	def save = {
		def announcementInstance = new Announcement()
		announcementInstance.properties = params
		announcementInstance.sentMessage = params.messageText
		def messages = messageSendService.getMessagesToSend(params)
		messages.each { message ->
			announcementInstance.addToMessages(message)
			announcementInstance.save()
			messageSendService.send(message)
		}
		announcementInstance.save(flush: true)
		flash.message = "Announcement has been saved and message(s) have been queued to send to " + messages*.dst.join(", ")
		[ownerId: announcementInstance.id]
	}
	
	def archive = {
		withAnnouncement { announcement ->
			announcement.archive()
			announcement.save(flush:true, failOnError:true)
		
			flash.message = "Announcement was archived successfully!"
			redirect(controller: "message", action: "inbox")
		}
	}
	
	def unarchive = {
		withAnnouncement { announcement ->
			announcement.unarchive()
			announcement.save()
		}

		flash.message = "Announcement was unarchived successfully!"
		redirect(controller: "archive", action: "folderList")
	}
	
	def confirmDelete = {
		def announcementInstance = Announcement.get(params.id)
		[announcementName:announcementInstance.name,
				announcementInstance: announcementInstance]
	}
	
	def delete = {
		withAnnouncement { announcement ->
			announcement.delete()
		}
		flash.message = "announcement has been permanently deleted!"
		redirect(controller:"message", action:"inbox")
	}

	private def withAnnouncement(Closure c) {
		def announcementInstance = Announcement.get(params.id)
		if (announcementInstance) c announcementInstance
		else render(text: "Could not find announcement with id ${params.id}") // TODO handle error state properly
	}
}
