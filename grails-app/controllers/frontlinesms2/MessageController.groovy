package frontlinesms2

import grails.util.GrailsConfig
import grails.converters.JSON

import frontlinesms2.MessageStatus

class MessageController {
	static allowedMethods = [save: "POST", update: "POST",
			delete: "POST", deleteAll: "POST",
			archive: "POST", archiveAll: "POST"]

	def messageSendService

	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.viewingArchive = params.viewingArchive ? params.viewingArchive.toBoolean() : false
		params.starred = params.starred ? params.starred.toBoolean() : false
		params.failed = params.failed ? params.failed.toBoolean() : false
		true
	}

	def index = {
		redirect(action:'inbox')
	}
	
	def getShowModel(messageInstanceList) {
		def messageInstance = (params.messageId) ? Fmessage.get(params.messageId) : messageInstanceList ? messageInstanceList[0]:null
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		def responseInstance = messageInstance?.messageOwner
		def checkedMessageCount = params.checkedMessageList?.tokenize(',')?.size()
		def selectedMessageList = params.checkedMessageList?: ',' + messageInstance?.id + ','
		[messageInstance: messageInstance,
				checkedMessageCount: checkedMessageCount,
				checkedMessageList: selectedMessageList,
				folderInstanceList: Folder.findAllByArchived(params.viewingArchive),
				responseInstance: responseInstance,
				pollInstanceList: Poll.findAllByArchived(params.viewingArchive),
				radioShows: RadioShow.findAll(),
				messageCount: Fmessage.countAllMessages(params),
				hasUndeliveredMessages: Fmessage.hasUndeliveredMessages()]
	}

	def inbox = {
		def messageInstanceList = Fmessage.inbox(params.starred, params.viewingArchive)
		render view:'standard', model:[messageInstanceList: messageInstanceList.list(params),
					messageSection: 'inbox',
					messageInstanceTotal: messageInstanceList.count(),
					actionLayout : (params.viewingArchive ? "archive" : "messages")] << getShowModel(messageInstanceList.list(params))
	}

	def sent = {
		def messageInstanceList = Fmessage.sent(params.starred, params.viewingArchive)
		render view:'standard', model:[messageSection: 'sent',
				messageInstanceList: messageInstanceList.list(params),
				messageInstanceTotal: messageInstanceList.count(),
				actionLayout : params.viewingArchive ? "archive" : "messages"] << getShowModel(messageInstanceList.list(params))
	}

	def pending = {
		def messageInstanceList = Fmessage.pending(params.failed)
		render view:'standard', model:[messageInstanceList: messageInstanceList.list(params),
				messageSection: 'pending',
				messageInstanceTotal: messageInstanceList.count(),
				failedMessageIds : Fmessage.findAllByStatus(MessageStatus.SEND_FAILED)*.id] << getShowModel(messageInstanceList.list(params))
	}
	
	def trash = {
		def messageInstanceList = Fmessage.deleted(params.starred)
		render view:'standard', model:[messageInstanceList: messageInstanceList.list(params),
					messageSection: 'trash',
					messageInstanceTotal: messageInstanceList.count()] << getShowModel(messageInstanceList.list(params))
	}

	def poll = {
		def pollInstance = Poll.get(params.ownerId)
		def messageInstanceList = pollInstance.getPollMessages(params.starred)
		render view:'../message/poll', model:[messageInstanceList: messageInstanceList.list(params),
				messageSection: 'poll',
				messageInstanceTotal: messageInstanceList.count(),
				ownerInstance: pollInstance,
				viewingMessages: params.viewingArchive ? params.viewingMessages : null,
				responseList: pollInstance.responseStats,
				pollResponse: pollInstance.responseStats as JSON,
				actionLayout : params.viewingArchive ? 'archive' : 'messages'] << getShowModel(messageInstanceList.list(params))
	}
	
	def radioShow = {
		def showInstance = RadioShow.get(params.ownerId)
		def messageInstanceList = showInstance?.getShowMessages(params.starred)

		render view:'standard', model:[messageInstanceList: messageInstanceList.list(params),
					messageSection: 'radioShow',
					messageInstanceTotal: messageInstanceList.count(),
					ownerInstance: showInstance] << getShowModel(messageInstanceList.list(params))
	}

	def folder = {
		def folderInstance = Folder.get(params.ownerId)
		def messageInstanceList = folderInstance?.getFolderMessages(params.starred)

		if(params.flashMessage) { flash.message = params.flashMessage }

		render view:'../message/standard', model:[messageInstanceList: messageInstanceList.list(params),
					messageSection: 'folder',
					messageInstanceTotal: messageInstanceList.count(),
					ownerInstance: folderInstance,
					viewingMessages: params.viewingArchive ? params.viewingMessages : null,
					actionLayout : params.viewingArchive ? 'archive' : 'messages'] << getShowModel(messageInstanceList.list(params))
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
			if(params.messageSection == 'result') redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
			else redirect(action: params.messageSection, params: [ownerId: params.ownerId, viewingArchive: params.viewingArchive, starred: params.starred, failed: params.failed])
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
