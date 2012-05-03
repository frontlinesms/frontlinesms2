package frontlinesms2

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
	
	def edit = {
		withActivity { activityInstance ->
			def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
			def activityType = activityInstance.shortName
			render view:"../$activityType/create", model:[contactList: Contact.list(),
				groupList:groupList,
				activityInstanceToEdit: activityInstance]
		}
	}
	
	def rename = {}
	
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
		flash.message = message(code: 'activity.archived.successfully')
		redirect(controller: "message", action: "inbox")
	}
	
	def unarchive = {
		withActivity { activity ->
			activity.unarchive()
			activity.save()
		} // TODO check if save is successful!
		flash.message = message(code: 'activity.unarchived.successfully')
		redirect(controller: "archive", action: "activityList")
	}
	
	def confirmDelete = {
		def activityInstance = Activity.get(params.id)
		model: [ownerName: activityInstance.name,
					ownerInstance: activityInstance]
	}
	
	def delete = {
		withActivity { activity ->
			TrashService.sendToTrash(activity)
		}
		flash.message = message(code: 'activity.trashed')
		redirect(controller:"message", action:"inbox")
	}
	
	def restore = {
		withActivity { activity ->
			activity.deleted = false
			Trash.findByObjectId(activity.id)?.delete()
			activity.save(failOnError: true, flush: true)
		}
		flash.message = message(code: 'activity.restored')
		redirect(controller: "message", action: "trash")
	}
	
	def create_new_activity = {}
	
	private def withActivity(Closure c) {
		def activityInstance = Activity.get(params.id)
		if (activityInstance) c activityInstance
		else render(text: message(code: 'activity.id.exist.not', args: [message(code: params.id), ''])) // TODO handle error state properly
	}
}
