package frontlinesms2

import grails.converters.*
import org.quartz.impl.triggers.SimpleTriggerImpl

class MessageController {
//> CONSTANTS
	static allowedMethods = [save: "POST", update: "POST", delete: "POST", archive: "POST"]

//> SERVICES
	def messageSendService
	def fmessageInfoService
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
	def beforeInterceptor = bobInterceptor
	
//> ACTIONS
	def index() {
		params.sort = 'date'
		redirect(action:'inbox', params:params)
	}
	
	def newMessageCount() {
		def section = params.messageSection
		if(!params.ownerId && section != 'trash') {
			def messageCount = [totalMessages:Fmessage."$section"(params.starred).count()]
			render messageCount as JSON
		} else if(section == 'activity') {
			def messageCount = [totalMessages:Activity.get(params.ownerId)?.getActivityMessages(params.starred)?.count()]
			render messageCount as JSON
		} else if(section == 'folder') {
			def messageCount = [totalMessages:Folder.get(params.ownerId)?.getFolderMessages(params.starred)?.count()]
			render messageCount as JSON
		} else
			render ""
	}

	def show() {
		def messageInstance = Fmessage.get(params.messageId)
		def activityInstance = Activity.get(params?.ownerId)
		messageInstance.read = true
		messageInstance.save()
		def model = [messageInstance: messageInstance,
				ownerInstance:activityInstance,
				folderInstanceList: Folder.findAllByArchivedAndDeleted(viewingArchive, false),
				activityInstanceList: Activity.findAllByArchivedAndDeleted(viewingArchive, false),
				messageSection: params.messageSection]
		render view:'/message/_single_message_details', model:model
	}

	def inbox() {
		def messageInstanceList = Fmessage.inbox(params.starred, this.viewingArchive)
		// check for flash message in parameters if there is none in flash.message
		flash.message = flash.message?:params.flashMessage
		render view:'../message/standard',
				model:[messageInstanceList: messageInstanceList.list(params),
						messageSection:'inbox',
						messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	}

	def sent() {
		def messageInstanceList = Fmessage.sent(params.starred, this.viewingArchive)
		render view:'../message/standard', model:[messageSection:'sent',
				messageInstanceList: messageInstanceList.list(params).unique(),
				messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	} 

	def pending() {
		def messageInstanceList = Fmessage.pending(params.failed)
		render view:'standard', model:[messageInstanceList: messageInstanceList.listDistinct(params),
				messageSection:'pending',
				messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	}
	
	def trash() {
		def trashedObject
		def trashInstanceList
		def messageInstanceList
		params.sort = params.sort?: 'date'
		if(params.id) {
			def setTrashInstance = { obj ->
				if(obj.objectClass == frontlinesms2.Fmessage) {
					params.messageId = obj.objectId
				} else {
					trashedObject = obj.object
				}
			}
			setTrashInstance(Trash.findById(params.id))
		}
		if(params.starred) {
			messageInstanceList = Fmessage.deleted(params.starred)
		} else {
			if(params.sort == 'date') params.sort = 'dateCreated'
			trashInstanceList = Trash.list(params)
		}
		render view:'standard', model:[trashInstanceList: trashInstanceList,
					messageInstanceList: messageInstanceList?.list(params),
					messageSection:'trash',
					messageInstanceTotal: Trash.count(),
					ownerInstance: trashedObject] << getShowModel()
	}

	def poll() { redirect(action: 'activity', params: params) }
	def announcement() { redirect(action: 'activity', params: params) }
	def autoreply() { redirect(action: 'activity', params: params) }
	def activity() {
		def activityInstance = Activity.get(params.ownerId)
		if (activityInstance) {
			def messageInstanceList = activityInstance.getActivityMessages(params.starred, true)
			def sentMessageCount = 0
			def sentDispatchCount = 0
			Fmessage.findAllByMessageOwnerAndInbound(activityInstance, false).each {
				sentDispatchCount += it.dispatches.size()
				sentMessageCount++
			}
			render view:"/activity/${activityInstance.shortName}/show",
				model:[messageInstanceList: messageInstanceList?.list(params),
						messageSection: 'activity',
						messageInstanceTotal: messageInstanceList?.count(),
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
			def messageInstanceList = folderInstance?.getFolderMessages(params.starred)
			if (params.flashMessage) { flash.message = params.flashMessage }
			render view:'../message/standard', model:[messageInstanceList: messageInstanceList.list(params),
						messageSection:'folder',
						messageInstanceTotal: messageInstanceList.count(),
						ownerInstance: folderInstance,
						viewingMessages: this.viewingArchive ? params.viewingMessages : null] << getShowModel()
		} else {
			flash.message = message(code: 'flash.message.folder.found.not')
			redirect(action: 'inbox')
		}
	}

	def send() {
		def fmessage = messageSendService.createOutgoingMessage(params)
		messageSendService.send(fmessage)
		flash.message = dispatchMessage 'queued', fmessage
		render(text: flash.message)
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
		params.flashMessage = dynamicMessage 'trashed', messages
		if (params.messageSection == 'result') {
			redirect(controller:'search', action:'result', params:
					[searchId:params.searchId, flashMessage:params.flashMessage])
		} else {
			println "Forwarding to action: $params.messageSection"
			redirect(controller:params.controller, action:params.messageSection, params:
					[ownerId:params.ownerId, starred:params.starred,
							failed:params.failed, searchId:params.searchId,
							flashMessage:params.flashMessage])
		}
	}
	
	def archive() {
		def messages = getCheckedMessages()
		messages.each { messageInstance ->
			if(!messageInstance.messageOwner) {
				messageInstance.archived = true
				messageInstance.save()
			}
		}
		params.flashMessage = dynamicMessage 'archived', messages
		if(params.messageSection == 'result') {
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, flashMessage: params.flashMessage])
		} else {
			redirect(controller: params.controller, action: params.messageSection, params: [ownerId: params.ownerId, starred: params.starred, failed: params.failed, searchId: params.searchId, flashMessage: params.flashMessage])
		}
	}
	
	def unarchive() {
		def messages = getCheckedMessages()
		messages.each { messageInstance ->
			if(!messageInstance.messageOwner) {
				messageInstance.archived = false
				messageInstance.save(failOnError: true)
			}
		}
		params.flashMessage = dynamicMessage 'unarchived', messages
		if(params.controller == 'search')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, messageId: params.messageId, flashMessage:params.flashMessage])
		else
			redirect(controller: 'archive', action: params.messageSection, params: [ownerId: params.ownerId, flashMessage:params.flashMessage])
	}

	def move() {
		def messagesToSend = []
		def activity = params.messageSection == 'activity'? Activity.get(params.ownerId): null
		def messageList = getCheckedMessages()
		messageList.each { messageInstance ->
			messageInstance.isDeleted = false
			Trash.findByObject(messageInstance)?.delete(failOnError:true)
			if (params.messageSection == 'activity') {
				messageInstance.messageOwner?.removeFromMessages(messageInstance)?.save()
				activity.addToMessages(messageInstance)
				if(activity.metaClass.hasProperty(null, 'autoreplyText') && activity.autoreplyText) {
					params.addresses = messageInstance.src
					params.messageText = activity.autoreplyText
					def outgoingMessage = messageSendService.createOutgoingMessage(params)
					outgoingMessage.save()
					messagesToSend << outgoingMessage
					activity.addToMessages(outgoingMessage)
				}
				activity.save(flush:true)
			} else if (params.ownerId && params.ownerId != 'inbox') {
				MessageOwner.get(params.ownerId).addToMessages(messageInstance).save()
			} else {
				messageInstance.with {
					if(messageOwner) {
						messageOwner.removeFromMessages(messageInstance).save()
						save()
					}
				}
			}
		}
		if(messagesToSend) {
			MessageSendJob.defer(messagesToSend)
		}
		flash.message = dynamicMessage 'updated', messageList
		render 'OK'
	}

	def changeResponse() {
		def responseInstance = PollResponse.get(params.responseId)
		def checkedMessages = getCheckedMessages()
		checkedMessages.each { messageInstance ->
			responseInstance.addToMessages(messageInstance)
		}
		responseInstance.poll.save()
		flash.message = dynamicMessage 'updated', checkedMessages
		render 'OK'
	}

	def changeStarStatus() {
		withFmessage { messageInstance ->
			messageInstance.starred =! messageInstance.starred
			messageInstance.save(failOnError: true)
			Fmessage.get(params.messageId).messageOwner?.refresh()
			params.remove('messageId')
			render(text: messageInstance.starred ? "starred" : "unstarred")
		}
	}
	
	def listRecipients() {
		def message = Fmessage.get(params.messageId)
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
	
	def unreadMessageCount() {
		render text:Fmessage.countUnreadMessages(), contentType:'text/plain'
	}

	def sendMessageCount() {
		render fmessageInfoService.getMessageInfos(params.message) as JSON
	}

//> PRIVATE HELPERS
	boolean isViewingArchive() { params.controller=='archive' }

	private def withFmessage(messageId = params.messageId, Closure c) {
		def m = Fmessage.get(messageId)
		if(m) c.call(m)
		else render(text:defaultMessage('notfound', messageId))
	}

	private def getShowModel(messageInstanceList) {
		def messageInstance = params.messageId? Fmessage.get(params.messageId): null
		messageInstance?.read = true
		messageInstance?.save()

		def checkedMessageCount = getCheckedMessageList().size()
		[messageInstance: messageInstance,
				checkedMessageCount: checkedMessageCount,
				activityInstanceList: Activity.findAllByArchivedAndDeleted(viewingArchive, false),
				folderInstanceList: Folder.findAllByArchivedAndDeleted(viewingArchive, false),
				messageCount: Fmessage.countAllMessages(params),
				hasFailedMessages: Fmessage.hasFailedMessages(),
				failedDispatchCount: messageInstance?.hasFailed ? Dispatch.findAllByMessageAndStatus(messageInstance, DispatchStatus.FAILED).size() : 0]
	}

	private def getCheckedMessages() {
		return Fmessage.getAll(getCheckedMessageList()) - null
	}

	private def getCheckedMessageList() {
		def checked = params['message-select']?: params.messageId?: []
		if(checked instanceof String) checked = checked.split(/\D+/) - ''
		if(checked instanceof Number) checked = [checked]
		if(checked.class.isArray()) checked = checked as List
		return checked
	}

	private def dispatchMessage(String code, Fmessage m) {
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

