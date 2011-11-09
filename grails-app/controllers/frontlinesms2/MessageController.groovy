package frontlinesms2

import grails.util.GrailsConfig
import grails.converters.JSON

import frontlinesms2.MessageStatus
import org.smslib.util.GsmAlphabet

class MessageController {
	static allowedMethods = [save: "POST", update: "POST",
			delete: "POST", deleteAll: "POST",
			archive: "POST", archiveAll: "POST"]

	def messageSendService
	def trashService

	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		if(params.action == sent || params.action == pending) params.sort = params.sort ?: 'dateSent'
		else params.sort = params.sort ?: 'dateReceived'
		params.order = params.order ?: 'desc'
		params.viewingArchive = params.viewingArchive ? params.viewingArchive.toBoolean() : false
		params.starred = params.starred ? params.starred.toBoolean() : false
		params.failed = params.failed ? params.failed.toBoolean() : false
		true
	}

	def index = {
		params.sort = 'dateReceived'
		redirect(action:'inbox', params:params)
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
				folderInstanceList: Folder.findAllByArchivedAndDeleted(params.viewingArchive, false),
				responseInstance: responseInstance,
				pollInstanceList: Poll.findAllByArchivedAndDeleted(params.viewingArchive, false),
				announcementInstanceList: Announcement.findAllByArchivedAndDeleted(params.viewingArchive, false),
				radioShows: RadioShow.findAll(),
				messageCount: Fmessage.countAllMessages(params),
				hasFailedMessages: Fmessage.hasFailedMessages()]
	}

	def inbox = {
		def messageInstanceList = Fmessage.inbox(params.starred, params.viewingArchive)
		render view:'standard', model:[messageInstanceList: messageInstanceList.list(params),
					messageSection: 'inbox',
					messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	}

	def sent = {
		def messageInstanceList = Fmessage.sent(params.starred, params.viewingArchive)
		render view:'standard', model:[messageSection: 'sent',
				messageInstanceList: messageInstanceList.list(params),
				messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	}

	def pending = {
		def messageInstanceList = Fmessage.pending(params.failed)
		render view:'standard', model:[messageInstanceList: messageInstanceList.list(params),
				messageSection: 'pending',
				messageInstanceTotal: messageInstanceList.count(),
				failedMessageIds : Fmessage.findAllByStatus(MessageStatus.SEND_FAILED)*.id] << getShowModel()
	}
	
	def trash = {
		def trashInstance
		def trashInstanceList
		def messageInstanceList
		params.sort = params.sort != "dateReceived" ? params.sort : 'dateCreated'
		if(params.id) {
			def setTrashInstance = { obj ->
				if(obj.objectType == "frontlinesms2.Fmessage") {
					params.messageId = obj.linkId
				} else {
					trashInstance = obj.link
				}
			}
			setTrashInstance(Trash.findById(params.id))
		}
		
		if(params.starred) {
			messageInstanceList = Fmessage.deleted(params.starred)
		} else {
			trashInstanceList =  Trash.list(params)
		}
		render view:'standard', model:[trashInstanceList:trashInstanceList,
					messageInstanceList: messageInstanceList?.list(params),
					messageSection: 'trash',
					messageInstanceTotal: Trash.count(),
					ownerInstance: trashInstance] << getShowModel()
	}

	def poll = {
		def pollInstance = Poll.get(params.ownerId)
		def messageInstanceList = pollInstance?.getPollMessages(params.starred)
		
		render view:'../message/poll', model:[messageInstanceList: messageInstanceList?.list(params),
				messageSection: 'poll',
				messageInstanceTotal: messageInstanceList?.count(),
				ownerInstance: pollInstance,
				viewingMessages: params.viewingArchive ? params.viewingMessages : null,
				responseList: pollInstance.responseStats,
				pollResponse: pollInstance.responseStats as JSON] << getShowModel()
	}
	
	def announcement = {
		def announcementInstance = Announcement.get(params.ownerId)
		def messageInstanceList = announcementInstance?.getAnnouncementMessages(params.starred)
		if(params.flashMessage) { flash.message = params.flashMessage }
		render view:'../message/standard', model:[messageInstanceList: messageInstanceList?.list(params),
					messageSection: 'announcement',
					messageInstanceTotal: messageInstanceList?.count(),
					ownerInstance: announcementInstance,
					viewingMessages: params.viewingArchive ? params.viewingMessages : null] << getShowModel()
	}
	
	def radioShow = {
		def showInstance = RadioShow.get(params.ownerId)
		def messageInstanceList = showInstance?.getShowMessages(params.starred)

		render view:'standard', model:[messageInstanceList: messageInstanceList?.list(params),
					messageSection: 'radioShow',
					messageInstanceTotal: messageInstanceList?.count(),
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
					viewingMessages: params.viewingArchive ? params.viewingMessages : null] << getShowModel()
	}

	def send = {
		def failedMessageIds = params.failedMessageIds
		def messages = failedMessageIds ? Fmessage.getAll([failedMessageIds].flatten()): messageSendService.getMessagesToSend(params)
		messages.each { message ->
			messageSendService.send(message)
		}
		flash.message = "Message has been queued to send to " + messages*.dst.join(", ")
		redirect (controller: "message", action: 'pending')
	}

	def delete = {
		def messageIdList = params.checkedMessageList ? params.checkedMessageList.tokenize(',') : [params.messageId]
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				messageInstance.deleted = true
				new Trash(identifier:messageInstance.contactName, message:messageInstance.text, objectType:messageInstance.class.name, linkId:messageInstance.id).save(failOnError: true, flush: true)
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
				redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
			}
			else redirect(action: params.messageSection, params: [ownerId: params.ownerId])
		}
	}

	def move = {
		def messageIdList = params.messageId.tokenize(',')
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				if (messageInstance.deleted == true) messageInstance.deleted = false
				if(Trash.findByLinkId(messageInstance.id)) {
					Trash.findByLinkId(messageInstance.id).delete(flush:true)
				}
				
				if (params.messageSection == 'poll')  {
					def unknownResponse = Poll.get(params.ownerId).responses.find { it.value == 'Unknown'}
					unknownResponse.addToMessages(messageInstance).save()
				} else if (params.messageSection == 'announcement') {
					Announcement.get(params.ownerId).addToMessages(messageInstance).save()
				} else if (params.messageSection == 'folder') {
					Folder.get(params.ownerId).addToMessages(messageInstance).save()
				} else {
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
		trashService.emptyTrash()
		redirect(action: 'inbox')
	}
	
	def getUnreadMessageCount = {
		render text: Fmessage.countUnreadMessages(), contentType:'text/plain'
	}
	
	def getSendMessageCount = {
		def message = params.message ?: ''
		def messageParts = GsmAlphabet.splitText(message, false)
		def messageCount = messageParts.size()>1 ? "${messageParts.size()} SMS messages": "1 SMS message"
		render text: "($messageCount)", contentType:'text/plain'
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
