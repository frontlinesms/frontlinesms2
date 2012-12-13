package frontlinesms2

import grails.converters.JSON


class ActivityController extends ControllerUtils {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def messageSendService
	def trashService

	def index() {
		redirect action:'create'
	}
	
	def create() {
		def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
		[contactList: Contact.list(),
				groupList:groupList, activityType: params.controller]
	}
	
	def edit() {
		withActivity { activityInstance ->
			def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
			def activityType = activityInstance.shortName
			render view:"../$activityType/create", model:[contactList: Contact.list(),
				groupList:groupList,
				activityInstanceToEdit: activityInstance, activityType: activityType]
		}
	}

	def rename() {}
	
	def update() {
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
	
	def archive() {
		withActivity { activity ->
			activity.archive()
			if(activity.save(flush:true)) {
				flash.message = defaultMessage 'archived'
				activity.deactivate()
			} else {
				flash.message = defaultMessage 'archive.failed', activity.id
			}
			redirect controller:"message", action:"inbox"
		}
	}
	
	def unarchive() {
		withActivity { activity ->
			activity.unarchive()
			if(activity.save()) {
				flash.message = defaultMessage 'unarchived'
				activity.activate()
			} else {
				if (activity instanceof Announcement)
					flash.message = defaultMessage 'unarchive.failed', activity.id
				else
					flash.message = defaultMessage 'unarchive.keyword.failed', activity.id
			}
			redirect controller:"archive", action:"activityList"
		}
	}
	
	def confirmDelete() {
		def activityInstance = Activity.get(params.id)
		model:[ownerName:activityInstance.name,
				ownerInstance:activityInstance]
	}
	
	def delete() {
		withActivity { activity ->
			trashService.sendToTrash(activity)
			activity.deactivate()
			flash.message = defaultMessage 'trashed'
			redirect controller:"message", action:"inbox"
		}
	}
	
	def restore() {
		withActivity { activity ->
			if(trashService.restore(activity)) {
				flash.message = defaultMessage 'restored'
				activity.activate()
			} else {
				flash.message = defaultMessage 'restore.failed', activity.id
			}
			redirect controller:"message", action:"trash"
		}
	}
	
	def create_new_activity() {}

	def getCollidingKeywords(topLevelKeywords, instance) {
		if (topLevelKeywords == null)
			return [:]
		def collidingKeywords = [:]
		def currentKeyword
		topLevelKeywords.toUpperCase().split(",").collect { it.trim() }.each { 
			currentKeyword = Keyword.getFirstLevelMatch(it)
			if(currentKeyword && (currentKeyword.activity.id != instance.id))
				collidingKeywords << [(currentKeyword.value):"'${currentKeyword.activity.name}'"]
		}
		println "colliding keywords:: $collidingKeywords"
		return collidingKeywords
	}

	protected void doSave(classShortname, service, instance) {
		try {
			service.saveInstance(instance, params)
			instance.activate()
			flash.message = message([code:"${instance.class.shortName}.save.success", args:[instance.name]])
			params.activityId = instance.id
			withFormat {
				json { render([ok:true, ownerId:instance.id] as JSON) }
				html { [ownerId:instance.id] }
			}
		} catch(Exception ex) {
			//ex.printStackTrace()
			def collidingKeywords = getCollidingKeywords(params.sorting == 'global'? '' : params.keywords, instance)
			def errors
			if (collidingKeywords) {
				errors = collidingKeywords.collect {
					if(it.key == '') {
						message(code:'activity.generic.global.keyword.in.use', args:[it.value])
					} else {
						message(code:'activity.generic.keyword.in.use', args:[it.key, it.value])
					}
				}.join('\n')
			} else {
				errors = instance.errors.allErrors.collect { message(error:it) }.join('\n')
			}
			withFormat {
				json { render([ok:false, text:errors] as JSON) }
			}
		}
	}
	
	private def withActivity = withDomainObject Activity

	private def defaultMessage(String code, args=[]) {
		def activityName = message code:'activity.label'
		return message(code:'default.' + code,
				args:[activityName] + args)
	}
}

