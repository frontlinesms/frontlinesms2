package frontlinesms2

import grails.converters.JSON

class ActivityController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def messageSendService
	def trashService

	def index = {
		redirect action:'create'
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
			if (activity.save()) {
				flash.message = defaultMessage 'updated'
		
				withFormat {
					json {
						render([ok:true] as JSON)
					}
				}
			} else {
				withFormat {
					json {
						render([ok:false, text:message(code: activity.errors.allErrors[0].codes[7])] as JSON)
					}
				}
			}
		}
	}
	
	def archive = {
		withActivity { activity ->
			activity.archive()
			if(activity.save(flush:true)) {
				flash.message = defaultMessage 'archived'
			} else {
				flash.message = defaultMessage 'archive.failed', activity.id
			}
			redirect controller:"message", action:"inbox"
		}
	}
	
	def unarchive = {
		withActivity { activity ->
			activity.unarchive()
			if(activity.save()) {
				flash.message = defaultMessage 'unarchived'
			} else {
				if (activity instanceof Announcement)
					flash.message = defaultMessage 'unarchive.failed', activity.id
				else
					flash.message = defaultMessage 'unarchive.keyword.failed', activity.id
			}
			redirect controller:"archive", action:"activityList"
		}
	}
	
	def confirmDelete = {
		def activityInstance = Activity.get(params.id)
		model:[ownerName:activityInstance.name,
				ownerInstance:activityInstance]
	}
	
	def delete = {
		withActivity { activity ->
			trashService.sendToTrash(activity)
			flash.message = defaultMessage 'trashed'
			redirect controller:"message", action:"inbox"
		}
	}
	
	def restore = {
		withActivity { activity ->
			activity.deleted = false
			Trash.findByObject(activity)?.delete()
			if(activity.save()) {
				flash.message = defaultMessage 'restored'
			} else {
				flash.message = defaultMessage 'restore.failed', activity.id
			}
			redirect controller:"message", action:"trash"
		}
	}
	
	def create_new_activity = {}
	
	private def withActivity(Closure c) {
		def activityInstance = Activity.get(params.id)
		if (activityInstance) c activityInstance
		else render(text: message(code:'activity.id.exist.not', args: [message(code: params.id), ''])) // TODO handle error state properly
	}

	private def defaultMessage(String code, args=[]) {
		def activityName = message code:'activity.label'
		return message(code:'default.' + code,
				args:[activityName] + args)
	}
}

