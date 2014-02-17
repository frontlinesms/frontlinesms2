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

	def show() {
		def missedCallInstance = MissedCall.get(params.missedCallId)
		def ownerInstance = missedCallOwner.get(params?.ownerId)
		missedCallInstance.read = true
		missedCallInstance.save()

		def model = [missedCallInstance: missedCallInstance,
				ownerInstance:ownerInstance,
				folderInstanceList: Folder.findAllByArchivedAndDeleted(viewingArchive, false),
				activityInstanceList: Activity.findAllByArchivedAndDeleted(viewingArchive, false),
				missedCallSection: params.missedCallSection]
		render view:'/missedCall/_single_missedCall_details', model:model
	}

	def inbox() {
		def missedCallInstanceList = MissedCall.inbox(params.starred, this.viewingArchive)
		render view:'../missedCall/standard',
				model:[missedCallInstanceList: missedCallInstanceList.list(params),
						missedCallSection:'inbox',
						missedCallInstanceTotal: missedCallInstanceList.count()] << getShowModel()
	}

	def trash() {
		def trashedObject
		def trashInstanceList
		def missedCallInstanceList
		params.sort = params.sort?: 'date'
		if(params.id) {
			def setTrashInstance = { obj ->
				if(obj.objectClass == "frontlinesms2.MissedCall") {
					params.missedCallId = obj.objectId
				} else {
					trashedObject = obj.object
				}
			}
			setTrashInstance(Trash.findById(params.id))
		}
		if(params.starred) {
			missedCallInstanceList = MissedCall.deleted(params.starred)
		} else {
			if(params.sort == 'date') params.sort = 'dateCreated'
			trashInstanceList = Trash.list(params)
		}
		render view:'standard', model:[trashInstanceList: trashInstanceList,
					missedCallInstanceList: missedCallInstanceList?.list(params),
					missedCallSection:'trash',
					missedCallInstanceTotal: Trash.count(),
					ownerInstance: trashedObject] << getShowModel()
	}

	def delete() {
		def missedCalls = getCheckedmissedCalls()
		missedCalls.each { m ->
			trashService.sendToTrash(m)
		}
		flash.missedCall = dynamicmissedCall 'trashed', missedCalls
		if (params.missedCallSection == 'result') {
			redirect(controller:'search', action:'result', params:
					[searchId:params.searchId])
		} else {
			log.info "Forwarding to action: $params.missedCallSection"
			redirect(controller:params.controller, action:params.missedCallSection, params:
					[ownerId:params.ownerId, starred:params.starred,
							failed:params.failed, searchId:params.searchId])
		}
	}
	
	def archive() {
		def missedCalls = getCheckedmissedCalls().findAll { !it.missedCallOwner && !it.hasPending }
		missedCalls.each { missedCallInstance ->
			missedCallInstance.archived = true
			missedCallInstance.save()
		}
		flash.missedCall = dynamicmissedCall 'archived', missedCalls
		if(params.missedCallSection == 'result') {
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
		} else {
			redirect(controller: params.controller, action: params.missedCallSection, params: [ownerId: params.ownerId, starred: params.starred, failed: params.failed, searchId: params.searchId])
		}
	}
	
	def unarchive() {
		def missedCalls = getCheckedmissedCalls()
		missedCalls.each { missedCallInstance ->
			if(!missedCallInstance.missedCallOwner) {
				missedCallInstance.archived = false
				missedCallInstance.save(failOnError: true)
			}
		}
		flash.missedCall = dynamicmissedCall 'unarchived', missedCalls
		if(params.controller == 'search')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, missedCallId: params.missedCallId])
		else
			redirect(controller: 'archive', action: params.missedCallSection, params: [ownerId: params.ownerId])
	}

	def changeStarStatus() {
		withMissedCall { missedCallInstance ->
			missedCallInstance.starred =! missedCallInstance.starred
			missedCallInstance.save(failOnError: true)
			MissedCall.get(params.missedCallId).missedCallOwner?.refresh()
			params.remove('missedCallId')
			render(text: missedCallInstance.starred ? "starred" : "unstarred")
		}
	}
	
	private def withMissedCall = withDomainObject MissedCall, { params.missedCallId }
}

