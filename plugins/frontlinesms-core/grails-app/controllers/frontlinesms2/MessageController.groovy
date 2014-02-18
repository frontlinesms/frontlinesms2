package frontlinesms2

import grails.converters.*


class MessageController extends ControllerUtils {
//> CONSTANTS
	static allowedMethods = [save: "POST", update: "POST", delete: "POST", archive: "POST"]

//> SERVICES
	def messageSendService
	def textMessageInfoService
	def trashService
	def textMessageService

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
		def interactionInstance = TextMessage.get(params.messageId)
		def ownerInstance = MessageOwner.get(params?.ownerId)
		interactionInstance.read = true
		interactionInstance.save()

		def model = [interactionInstance: interactionInstance,
				ownerInstance:ownerInstance,
				folderInstanceList: Folder.findAllByArchivedAndDeleted(viewingArchive, false),
				activityInstanceList: Activity.findAllByArchivedAndDeleted(viewingArchive, false),
				messageSection: params.messageSection]
		render view:'/interaction/_single_message_details', model:model
	}

	def inbox() {
		def interactionInstanceList = TextMessage.inbox(params.starred, this.viewingArchive)
		render view:'../message/standard',
				model:[interactionInstanceList: interactionInstanceList.list(params),
						messageSection:'inbox',
						interactionInstanceTotal: interactionInstanceList.count()] << getShowModel()
	}

	def sent() {
		def interactionInstanceList = TextMessage.sent(params.starred, this.viewingArchive)
		render view:'../message/standard', model:[messageSection:'sent',
				interactionInstanceList: interactionInstanceList.list(params).unique(),
				interactionInstanceTotal: interactionInstanceList.count()] << getShowModel()
	} 

	def pending() {
		render view:'standard', model:[interactionInstanceList:TextMessage.listPending(params.failed, params),
				messageSection:'pending',
				interactionInstanceTotal: TextMessage.countPending()] << getShowModel()
	}
	
	def trash() {
		def trashedObject
		def trashInstanceList
		def interactionInstanceList
		params.sort = params.sort?: 'date'
		if(params.id) {
			def setTrashInstance = { obj ->
				if(obj.objectClass == "frontlinesms2.TextMessage") {
					params.messageId = obj.objectId
				} else {
					trashedObject = obj.object
				}
			}
			setTrashInstance(Trash.findById(params.id))
		}
		if(params.starred) {
			interactionInstanceList = TextMessage.deleted(params.starred)
		} else {
			if(params.sort == 'date') params.sort = 'dateCreated'
			trashInstanceList = Trash.list(params)
		}
		render view:'standard', model:[trashInstanceList: trashInstanceList,
					interactionInstanceList: interactionInstanceList?.list(params),
					messageSection:'trash',
					interactionInstanceTotal: Trash.count(),
					ownerInstance: trashedObject] << getShowModel()
	}

	def poll() { redirect(action: 'activity', params: params) }
	def webconnection() { redirect(action: 'activity', params: params) }
	def autoforward() { redirect(action: 'activity', params: params) }
	def customactivity() { redirect(action: 'activity', params: params) }
	def announcement() { redirect(action: 'activity', params: params) }
	def autoreply() { redirect(action: 'activity', params: params) }
	def subscription() { redirect(action: 'activity', params: params) }
	def activity() {
		def activityInstance = Activity.get(params.ownerId)
		if (activityInstance) {
			if (params.starred == null) params.starred = false
			if (params.failed == null) params.failed = false
			def getSent = params.containsKey("inbound") ? Boolean.parseBoolean(params.inbound) : null
			def interactionInstanceList = activityInstance.getActivityMessages(params.starred, getSent, params.stepId, params)
			def sentMessageCount = 0
			def sentDispatchCount = 0
			TextMessage.findAllByMessageOwnerAndInbound(activityInstance, false).each {
				sentDispatchCount += it.dispatches.size()
				sentMessageCount++				
			}
			render view:"/activity/${activityInstance.shortName}/show",
				model:[interactionInstanceList: interactionInstanceList,
						messageSection: params.messageSection?:'activity',
						interactionInstanceTotal: activityInstance.getMessageCount(params.starred, getSent),
						stepInstance:Step.get(params.stepId),
						ownerInstance: activityInstance,
						viewingMessages: this.viewingArchive ? params.viewingMessages : null,
						pollResponse: activityInstance instanceof Poll ? activityInstance.responseStats as JSON : null,
						sentMessageCount: sentMessageCount,
						sentDispatchCount: sentDispatchCount] << getShowModel()
		} else {
			flash.message = message(code: 'flash.message.activity.found.not')
			redirect(action: 'inbox')
		}
	}
	
	def folder() {
		def folderInstance = Folder.get(params.ownerId)
		if (folderInstance) {
			if (params.starred == null) params.starred = false
			def getSent = params.containsKey("inbound") ? Boolean.parseBoolean(params.inbound) : null
			def interactionInstanceList = folderInstance?.getFolderMessages(params.starred, getSent)
			render view:'../message/standard', model:[interactionInstanceList: interactionInstanceList.list(params),
						messageSection:'folder',
						interactionInstanceTotal: interactionInstanceList.count(),
						ownerInstance: folderInstance,
						viewingMessages: this.viewingArchive ? params.viewingMessages : null] << getShowModel()
		} else {
			flash.message = message(code: 'flash.message.folder.found.not')
			redirect(action: 'inbox')
		}
	}

	def send() {
		def textMessage = messageSendService.createOutgoingMessage(params)
		messageSendService.send(textMessage)
		render(text: dispatchMessage('queued', textMessage))
	}
	
	def retry() {
		def messages = getCheckedMessages()
		def dispatchCount = 0
		messages.each { m ->
			dispatchCount += messageSendService.retry(m)
		}
		flash.message = message code:'fmessage.retry.success.multiple', args:[dispatchCount]
		redirect controller:'message', action:'pending'
	}
	
	def delete() {
		def messages = getCheckedMessages()
		messages.each { m ->
			trashService.sendToTrash(m)
		}
		flash.message = dynamicMessage 'trashed', messages
		if (params.messageSection == 'result') {
			redirect(controller:'search', action:'result', params:
					[searchId:params.searchId])
		} else {
			log.info "Forwarding to action: $params.messageSection"
			redirect(controller:params.controller, action:params.messageSection, params:
					[ownerId:params.ownerId, starred:params.starred,
							failed:params.failed, searchId:params.searchId])
		}
	}
	
	def archive() {
		def messages = getCheckedMessages().findAll { !it.messageOwner && !it.hasPending }
		messages.each { interactionInstance ->
			interactionInstance.archived = true
			interactionInstance.save()
		}
		flash.message = dynamicMessage 'archived', messages
		if(params.messageSection == 'result') {
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
		} else {
			redirect(controller: params.controller, action: params.messageSection, params: [ownerId: params.ownerId, starred: params.starred, failed: params.failed, searchId: params.searchId])
		}
	}
	
	def unarchive() {
		def messages = getCheckedMessages()
		messages.each { interactionInstance ->
			if(!interactionInstance.messageOwner) {
				interactionInstance.archived = false
				interactionInstance.save(failOnError: true)
			}
		}
		flash.message = dynamicMessage 'unarchived', messages
		if(params.controller == 'search')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, messageId: params.messageId])
		else
			redirect(controller: 'archive', action: params.messageSection, params: [ownerId: params.ownerId])
	}

	def move() {
		def activity = params.messageSection == 'activity'? Activity.get(params.ownerId): null
		def messageList = getCheckedMessages()
		textMessageService.move(messageList, activity, params)
		flash.message = dynamicMessage 'updated', messageList
		render 'OK'
	}

	def changeResponse() {
		def responseInstance = PollResponse.get(params.responseId)
		def checkedMessages = getCheckedMessages()
		checkedMessages.each { interactionInstance ->
			responseInstance.addToMessages(interactionInstance)
		}
		responseInstance.poll.save()
		flash.message = dynamicMessage 'updated', checkedMessages
		render 'OK'
	}

	def changeStarStatus() {
		withTextMessage { interactionInstance ->
			interactionInstance.starred =! interactionInstance.starred
			interactionInstance.save(failOnError: true)
			TextMessage.get(params.messageId).messageOwner?.refresh()
			params.remove('messageId')
			render(text: interactionInstance.starred ? "starred" : "unstarred")
		}
	}
	
	def listRecipients() {
		def message = TextMessage.get(params.messageId)
		if(!message) {
			render text:'ERROR'
			return
		}
		render message.dispatches.collect {
			String display = Contact.findByMobile(it.dst)?.name?: it.dst
			[display:display, status:it.status.toString()]
		}.sort { it.display } as JSON
	}

	def confirmEmptyTrash() {}
	
	def emptyTrash() {
		trashService.emptyTrash()
		redirect action:'inbox'
	}

	def sendMessageCount() {
		render textMessageInfoService.getMessageInfos(params.message) as JSON
	}

//> PRIVATE HELPERS
	boolean isViewingArchive() { params.controller=='archive' }

	private def withTextMessage = withDomainObject TextMessage, { params.messageId }

	private def getShowModel() {
		def interactionInstance = params.messageId? TextMessage.get(params.messageId): null
		interactionInstance?.read = true
		interactionInstance?.save()

		def checkedMessageCount = getCheckedMessageList().size()
		[interactionInstance: interactionInstance,
				checkedMessageCount: checkedMessageCount,
				activityInstanceList: Activity.findAllByArchivedAndDeleted(viewingArchive, false),
				folderInstanceList: Folder.findAllByArchivedAndDeleted(viewingArchive, false),
				messageCount: TextMessage.countAllMessages(),
				hasFailedMessages: TextMessage.hasFailedMessages(),
				failedDispatchCount: interactionInstance?.hasFailed ? Dispatch.findAllByMessageAndStatus(interactionInstance, DispatchStatus.FAILED).size() : 0]
	}

	private def getCheckedMessages() {
		return TextMessage.getAll(getCheckedMessageList()) - null
	}

	private def getCheckedMessageList() {
		def checked = params['message-select']?: params.messageId?: []
		if(checked instanceof String) checked = checked.split(/\D+/) - ''
		if(checked instanceof Number) checked = [checked]
		if(checked.class.isArray()) checked = checked as List
		return checked
	}

	private def dispatchMessage(String code, TextMessage m) {
		def args
		code = 'fmessage.' + code
		if(m.dispatches.size() == 1) {
			args = [m.displayName]
		} else {
			code += '.multiple'
			args = [m.dispatches.size()]
		}
		return message(code:code, args:args)
	}

	private def dynamicMessage(String code, def list) {
		def count = list.size()
		if(count == 1) defaultMessage code
		else pluralMessage code, count
	}

	private def defaultMessage(String code, Object... args=[]) {
		def messageName = message code:'fmessage.label'
		return message(code:'default.' + code,
				args:[messageName] + args)
	}

	private def pluralMessage(String code, count, Object... args=[]) {
		def messageName = message code:'fmessage.label.multiple', args:[count]
		return message(code:'default.' + code + '.multiple',
				args:[messageName] + args)
	}
}

