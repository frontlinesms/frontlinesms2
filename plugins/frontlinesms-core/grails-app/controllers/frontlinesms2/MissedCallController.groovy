package frontlinesms2

import grails.converters.*


class missedCallController extends ControllerUtils {
//> CONSTANTS
	static allowedMethods = [save: "POST", update: "POST", delete: "POST", archive: "POST"]

//> SERVICES
	def trashService

//> INTERCEPTORS
	def bobInterceptor = {
		params.sort = params.sort ?: 'date'
		params.order = params.order ?: 'desc'
		params.starred = params.starred ? params.starred.toBoolean() : false
		params.failed = params.failed ? params.failed.toBoolean() : false
		params.max = params.max?: grailsApplication.config.grails.views.pagination.max
		params.offset  = params.offset ?: 0
		return true
	}
	def beforeInterceptor = [except:'index', action:bobInterceptor]
	
//> ACTIONS
	def index() {
		redirect action:'inbox', params:params
	}

	def missedCalls() {
		redirect action:'inbox', params:params
	}

	def show() {
		def interactionInstance = MissedCall.get(params.interactionId)
		interactionInstance.read = true
		interactionInstance.save()

		def model = [interactionInstance: interactionInstance,
				folderInstanceList: Folder.findAllByArchivedAndDeleted(false, false),
				activityInstanceList: Activity.findAllByArchivedAndDeleted(false, false),
				missedCallSection: 'inbox'] // TODO correct this when missedCalls can enter other sections
		render view:'/missedCall/_single_interaction_details', model:model
	}

	def inbox() {
		def interactionInstanceList = MissedCall.inbox(params.starred)
		render view:'../missedCall/standard',
				model:[interactionInstanceList: interactionInstanceList.list(params),
						messageSection:'missedCalls',
						interactionInstanceTotal: interactionInstanceList.count()] << getShowModel()
	}

	def trash() {
		def trashedObject
		def trashInstanceList
		def interactionInstanceList
		params.sort = params.sort?: 'date'
		if(params.id) {
			def setTrashInstance = { obj ->
				if(obj.objectClass == "frontlinesms2.MissedCall") {
					params.interactionId = obj.objectId
				} else {
					trashedObject = obj.object
				}
			}
			setTrashInstance(Trash.findById(params.id))
		}
		if(params.starred) {
			interactionInstanceList = MissedCall.deleted(params.starred)
		} else {
			if(params.sort == 'date') params.sort = 'dateCreated'
			trashInstanceList = Trash.list(params)
		}
		render view:'standard', model:[trashInstanceList: trashInstanceList,
					interactionInstanceList: interactionInstanceList?.list(params),
					missedCallSection:'trash',
					interactionInstanceTotal: Trash.count(),
					ownerInstance: trashedObject] << getShowModel()
	}

	def delete() {
		def missedCalls = getCheckedMissedCalls()
		missedCalls.each { m ->
			trashService.sendToTrash(m)
		}
		flash.message = dynamicMessage 'trashed', missedCalls
		redirect(controller:params.controller, action:params.missedCallSection, params:
				[ownerId:params.ownerId, starred:params.starred,
							failed:params.failed, searchId:params.searchId])
	}
	
	def archive() {
		def missedCalls = getCheckedMissedCalls().findAll { !it.missedCallOwner && !it.hasPending }
		missedCalls.each { interactionInstance ->
			interactionInstance.archived = true
			interactionInstance.save()
		}
		flash.message = dynamicMessage 'archived', missedCalls
		if(params.missedCallSection == 'result') {
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
		} else {
			redirect(controller: params.controller, action: params.missedCallSection, params: [ownerId: params.ownerId, starred: params.starred, failed: params.failed, searchId: params.searchId])
		}
	}
	
	def unarchive() {
		def missedCalls = getCheckedMissedCalls()
		missedCalls.each { interactionInstance ->
			if(!interactionInstance.missedCallOwner) {
				interactionInstance.archived = false
				interactionInstance.save(failOnError: true)
			}
		}
		flash.message = dynamicMessage 'unarchived', missedCalls
		if(params.controller == 'search')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, interactionId: params.interactionId])
		else
			redirect(controller: 'archive', action: params.missedCallSection, params: [ownerId: params.ownerId])
	}

	def changeStarStatus() {
		withMissedCall { interactionInstance ->
			interactionInstance.starred =! interactionInstance.starred
			interactionInstance.save(failOnError: true)
			MissedCall.get(params.interactionId).missedCallOwner?.refresh()
			params.remove('interactionId')
			render(text: interactionInstance.starred ? "starred" : "unstarred")
		}
	}
	
	private def withMissedCall = withDomainObject MissedCall, { params.interactionId }

	private def getShowModel() {
		def interactionInstance = params.interactionId? MissedCall.get(params.interactionId): null
		interactionInstance?.read = true
		interactionInstance?.save()

		def checkedMissedCallCount = getCheckedMissedCallList().size()
		[interactionInstance: interactionInstance,
				checkedMissedCallCount: checkedMissedCallCount,
				activityInstanceList: Activity.findAllByArchivedAndDeleted(false, false),
				folderInstanceList: Folder.findAllByArchivedAndDeleted(false, false),
				messageCount: TextMessage.countAllMessages(),
				missedCallCount: MissedCall.countAllMissedCalls()]
	}

	private def getCheckedMissedCalls() {
		return MissedCall.getAll(getCheckedMissedCallList()) - null
	}

	private def getCheckedMissedCallList() {
		def checked = params['interaction-select']?: params.interactionId?: []
		if(checked instanceof String) checked = checked.split(/\D+/) - ''
		if(checked instanceof Number) checked = [checked]
		if(checked.class.isArray()) checked = checked as List
		return checked
	}

	private def dynamicMessage(String code, def list) {
		def count = list.size()
		if(count == 1) defaultMessage code
		else pluralMessage code, count
	}

	private def defaultMessage(String code, Object... args=[]) {
		def messageName = message code:'missedCall.label'
		return message(code:'default.' + code,
				args:[messageName] + args)
	}

	private def pluralMessage(String code, count, Object... args=[]) {
		def messageName = message code:'missedCall.label.multiple', args:[count]
		return message(code:'default.' + code + '.multiple',
				args:[messageName] + args)
	}
}

