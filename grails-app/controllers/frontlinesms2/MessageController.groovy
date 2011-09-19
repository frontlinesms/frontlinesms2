package frontlinesms2

import grails.util.GrailsConfig
import grails.converters.JSON

import frontlinesms2.MessageStatus

class MessageController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST", deleteAll: "POST",
							archive: "POST", archiveAll: "POST"]

	def messageSendService

	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.archived = params.archived ? params.archived.toBoolean() : false
		true
	}

	def index = {
		redirect(action:'inbox')
	}

	def show = { messageInstanceList ->
		def messageInstance = params.messageId ? Fmessage.get(params.messageId) : messageInstanceList ? messageInstanceList[0]:null
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		def responseInstance, selectedMessageList
		if (messageInstance?.messageOwner) { responseInstance = messageInstance.messageOwner }
		def checkedMessageCount = params.checkedMessageList?.tokenize(',')?.size()
		if (!params.checkedMessageList) selectedMessageList = ',' + messageInstance?.id + ','
		else selectedMessageList = params.checkedMessageList
		[messageInstance: messageInstance,
				checkedMessageCount: checkedMessageCount,
				checkedMessageList: selectedMessageList,
				folderInstanceList: Folder.findAll(),
				responseInstance: responseInstance,
				pollInstanceList: Poll.getNonArchivedPolls(),
				radioShows: RadioShow.findAll(),
				messageCount: Fmessage.countAllMessages(params),
				hasUndeliveredMessages: Fmessage.hasUndeliveredMessages()]
	}

	def trash = {
		def messageInstanceList = Fmessage.getDeletedMessages(params)
			[messageInstanceList: messageInstanceList,
					messageSection: 'trash',
					messageInstanceTotal: Fmessage.countDeletedMessages(params.starred)] << show(messageInstanceList)
	}

	def inbox = {
		def messageInstanceList = Fmessage.getInboxMessages(params)
		[messageInstanceList: messageInstanceList,
					messageSection: 'inbox',
					messageInstanceTotal: Fmessage.countInboxMessages(params),
					actionLayout : (params.archived ? "archive" : "messages")] << show(messageInstanceList)
	}

	def sent = {
		def messageInstanceList = Fmessage.getSentMessages(params)
		[messageSection: 'sent',
				messageInstanceList: messageInstanceList,
				messageInstanceTotal: Fmessage.countSentMessages(params),
				actionLayout : params.archived ? "archive" : "messages"] << show(messageInstanceList)
	}

	def pending = {
		def messageInstanceList = Fmessage.getPendingMessages(params)
		[messageInstanceList: messageInstanceList,
				messageSection: 'pending',
				messageInstanceTotal: Fmessage.countPendingMessages(params.failed),
				failedMessageIds : Fmessage.findAllByStatus(MessageStatus.SEND_FAILED)*.id] << show(messageInstanceList)
	}

	def poll = {
		def activityInstance = Poll.get(params.ownerId)
		def messageInstanceList = activityInstance.getMessages(params)		
		[messageInstanceList: messageInstanceList,
				messageSection: 'poll',
				messageInstanceTotal: activityInstance.countMessages(params.starred),
				ownerInstance: activityInstance,
				responseList: activityInstance.responseStats,
				pollResponse: activityInstance.responseStats as JSON,
				actionLayout : params.archived ? 'archive' : 'messages'] << show(messageInstanceList)
	}

	def folder = {
		def folderInstance = Folder.get(params.ownerId)
		def messageInstanceList = folderInstance?.getFolderMessages(params)

		if(params.flashMessage) { flash.message = params.flashMessage }

		[messageInstanceList: messageInstanceList,
				messageSection: 'folder',
				messageInstanceTotal: folderInstance.countMessages(params.starred),
				ownerInstance: folderInstance] << show(messageInstanceList)
	}

	def radioShow = {
		def showInstance = RadioShow.get(params.ownerId)
		def messageInstanceList = showInstance?.getShowMessages(params)

		[messageInstanceList: messageInstanceList,
				messageSection: 'radioShow',
				messageInstanceTotal: showInstance.countMessages(params.starred),
				ownerInstance: showInstance] << show(messageInstanceList)
	}

	def send = {
		def failedMessageIds = params.failedMessageIds
		def messages = failedMessageIds ? Fmessage.getAll([failedMessageIds].flatten()): getMessagesToSend()
		messages.each { message ->
			messageSendService.send(message)
		}
		flash.message = "Message has been queued to send to " + messages*.dst.join(", ")
		redirect (action: 'sent')
	}

	def getMessagesToSend() {
		def messages = []
		def addresses = [params.addresses].flatten() - null
		def groups = [params.groups].flatten() - null
		addresses += groups.collect {Group.findByName(it).getAddresses()}.flatten()
		addresses.unique().each { address ->
			//TODO: Need to add source from app setting
			messages << new Fmessage(src: "src", dst: address, text: params.messageText)
		}
		return messages
	}

	def delete = {
		def messageIdList = params.checkedMessageList ? params.checkedMessageList.tokenize(',') : [params.messageId]
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				messageInstance.toDelete()
				messageInstance.save(failOnError: true, flush: true)
			}
		}
		flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: ''), messageIdList.size() + ' messages'])}"
		if (isAjaxRequest()) {
			render ""
		}else {
			if(params.messageSection == 'result') {
				redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
			}
			else redirect(action: params.messageSection, params: [ownerId: params.ownerId,archived: params.archived])
		}
	}

	def archive = {
		def messageIdList = params.checkedMessageList ? params.checkedMessageList.tokenize(',') : [params.messageId]
		def listSize = messageIdList.size();
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				if(!messageInstance.messageOwner) {
					messageInstance.archive()
					messageInstance.save(failOnError: true, flush: true)
				} else {
					listSize--
				}
			}
		}
		flash.message = "${message(code: 'default.archived.message', args: [message(code: 'message.label', default: ''), listSize + ' messages'])}"
		if (isAjaxRequest()) {
			render ""
		}else {
			if(params.messageSection == 'result') {
				println "Search params: $params"
				redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
			}
			else redirect(action: params.messageSection, params: [ownerId: params.ownerId])
		}
	}

	def move = {
		def messageIdList = params.messageId.tokenize(',')
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				if (params.messageSection == 'poll')  {
					def unknownResponse = Poll.get(params.ownerId).responses.find { it.value == 'Unknown'}
					unknownResponse.addToMessages(messageInstance).save()
				}
				else if (params.messageSection == 'folder')
					Folder.get(params.ownerId).addToMessages(messageInstance).save()
				else {
					messageInstance.with {
						messageOwner?.removeFromMessages messageInstance
						messageOwner = null
						status = MessageStatus.INBOUND
						messageOwner?.save()
						save()
					}
				}
			}
		}
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: ''), messageIdList.size() + ' messages'])}"
		render ""
	}

	def changeResponse = {
		def messageIdList = params.messageId.tokenize(',')
		messageIdList.each { id ->
			withFmessage id, { messageInstance ->
				def responseInstance = PollResponse.get(params.responseId)
				responseInstance.addToMessages(messageInstance).save(failOnError: true, flush: true)
			}
		}
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: 'Fmessage'), 'messages'])}"
		render ""
	}

	def changeStarStatus = {
		withFmessage { messageInstance ->
			messageInstance.starred ? messageInstance.removeStar() : messageInstance.addStar()
			messageInstance.save(failOnError: true, flush: true)
			Fmessage.get(params.messageId).messageOwner?.refresh()
            params.remove('messageId')
			render(text: messageInstance.starred ? "starred" : "unstarred")
		}
	}

	def confirmEmptyTrash = { }
	
	def emptyTrash = {
		Fmessage.findAllByDeleted(true)*.delete()
		redirect(action: 'inbox')
	}

	private def withFmessage(messageId = params.messageId, Closure c) {
			def m = Fmessage.get(messageId)
			if(m) c.call(m)
			else render(text: "Could not find message with id ${params.messageId}") // TODO handle error state properly
	}

	private def isAjaxRequest() {
		return request.xhr
	}
}
