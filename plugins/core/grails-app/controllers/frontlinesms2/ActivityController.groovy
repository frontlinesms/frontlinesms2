package frontlinesms2

import groovy.lang.Closure;

class ActivityController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	def messageSendService

    def index = {
		redirect(action:'create')
	}
	
	def create = {
		def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
		[contactList: Contact.list(),
			groupList:groupList]
	}
	
	def rename = {
	}
	
	def update = {
		withActivity { activity ->
			activity.properties = params
			activity.save() // TODO check if save is successful
		}
		redirect(controller: "message", action: "activity", params: [ownerId: params.id])
	}
	
	def archive = {
		withActivity { activity ->
			activity.archive()
			activity.save(flush:true, failOnError:true)
		} // TODO don't flush; check if save is successful
		flash.message = "Activity archived successfully!"
		redirect(controller: "message", action: "inbox")
	}
	
	def unarchive = {
		withActivity { activity ->
			activity.unarchive()
			activity.save()
		} // TODO check if save is successful!
		flash.message = "Activity unarchived successfully!"
		redirect(controller: "archive", action: "activityList",  params:[viewingArchive: true])
	}
	
	def confirmDelete = {
		def activityInstance = Activity.get(params.id)
		model: [ownerName: activityInstance.name,
					ownerInstance: activityInstance]
	}
	
	def delete = {
		withActivity { activity ->
			activity.deleted = true
			activity.messages.each {
				it.isDeleted = true
				it.save(flush: true)
			}
			new Trash(identifier:activity.name, message:"${activity.liveMessageCount}", objectType:activity.class.name, linkId:activity.id).save(failOnError: true, flush: true)
			activity.save(failOnError: true, flush: true)
		}
		flash.message = "Activity has been trashed!"
		redirect(controller:"message", action:"inbox")
	}
	
	def restore = {
		withActivity { activity ->
			activity.deleted = false
			Trash.findByLinkId(activity.id)?.delete()
			activity.save(failOnError: true, flush: true)
		}
		flash.message = "Activity has been restored!"
		redirect(controller: "message", action: "trash")
	}
	
	def create_new_activity = {
	}
	
	private def withActivity(Closure c) {
		def activityInstance = Activity.get(params.id)
		if (activityInstance) c activityInstance
		else render(text: "Could not find activity with id ${params.id}") // TODO handle error state properly
	}
}
