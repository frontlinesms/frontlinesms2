package frontlinesms2

import grails.util.GrailsConfig
import grails.converters.JSON
import javax.servlet.http.HttpServletRequest

class MessageController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def messageSendService

	def beforeInterceptor = {
		params['max'] = params['max'] ?: getPaginationCount()
		params['offset']  = params['offset'] ?: 0
		params['archived'] = params['archived'] ? params['archived'].toBoolean()  : false
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
		def responseInstance
		if (messageInstance?.messageOwner) { responseInstance = messageInstance.messageOwner }
		
		[messageInstance: messageInstance,
				folderInstanceList: Folder.findAll(),
				responseInstance: responseInstance,
				pollInstanceList: Poll.getNonArchivedPolls(),
				radioShows: RadioShow.findAll(),
				messageCount: Fmessage.countAllMessages(params)]
	}

	def trash = {
		def messageInstanceList = Fmessage.getDeletedMessages(params)
			[messageInstanceList: messageInstanceList,
					messageSection: 'trash',
					messageInstanceTotal: Fmessage.countDeletedMessages(params['starred'])] << show(messageInstanceList)
	}

	def inbox = {
		def messageInstanceList = Fmessage.getInboxMessages(params)
		[messageInstanceList: messageInstanceList,
					messageSection: 'inbox',
					messageInstanceTotal: Fmessage.countInboxMessages(params),
					actionLayout : (params['archived'] ? "archive" : "messages")] << show(messageInstanceList)
	}

	def sent = {
		def messageInstanceList = Fmessage.getSentMessages(params)
		[messageSection: 'sent',
				messageInstanceList: messageInstanceList,
				messageInstanceTotal: Fmessage.countSentMessages(params),
				actionLayout : params['archived'] ? "archive" : "messages"] << show(messageInstanceList)
	}

	def pending = {
		def messageInstanceList = Fmessage.getPendingMessages(params)
		[messageInstanceList: messageInstanceList,
				messageSection: 'pending',
				messageInstanceTotal: Fmessage.countPendingMessages(params['starred'])] << show(messageInstanceList)
	}

	def poll = {
		def ownerInstance = Poll.get(params.ownerId)
		def messageInstanceList = ownerInstance.getMessages(params)		
		[messageInstanceList: messageInstanceList,
				messageSection: 'poll',
				messageInstanceTotal: ownerInstance.countMessages(params['starred']),
				ownerInstance: ownerInstance,
				responseList: ownerInstance.responseStats,
				pollResponse: ownerInstance.responseStats as JSON,
				actionLayout : params['archived'] ? 'archive' : 'messages'] << show(messageInstanceList)
	}
	
	def folder = {
		def folderInstance = Folder.get(params.ownerId)
		def messageInstanceList = folderInstance?.getFolderMessages(params)

		if(params.flashMessage) { flash.message = params.flashMessage }

		[messageInstanceList: messageInstanceList,
				messageSection: 'folder',
				messageInstanceTotal: folderInstance.countMessages(params['starred']),
				ownerInstance: folderInstance] << show(messageInstanceList)
	}

	def radioShow = {
		def showInstance = RadioShow.get(params.ownerId)
		def messageInstanceList = showInstance?.getShowMessages(params)

		[messageInstanceList: messageInstanceList,
				messageSection: 'radioShow',
				messageInstanceTotal: showInstance.countMessages(params['starred']),
				ownerInstance: showInstance] << show(messageInstanceList)
	}

	def move = {
		def ids = [params.ids].flatten()
		ids.each {id ->
			withFmessage id, {messageInstance ->
				def messageOwner
				if (params.messageSection == 'poll') {
					messageOwner = Poll.get(params.ownerId)
				} else if (params.messageSection == 'folder') {
					messageOwner = Folder.get(params.ownerId)
				}
				if (messageOwner instanceof Poll) {
					def unknownResponse = messageOwner.getResponses().find { it.value == 'Unknown'}
					unknownResponse.addToMessages(Fmessage.get(params.messageId) ?: messageInstance).save(failOnError: true, flush: true)
				} else if (messageOwner instanceof Folder) {
					messageOwner.addToMessages(Fmessage.get(params.messageId) ?: messageInstance).save(failOnError: true, flush: true)
				}
			}
		}
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: ''), ids.size() + ' messages'])}"
		render ""
	}

	def changeResponse = {
		withFmessage { messageInstance ->
			def responseInstance = PollResponse.get(params.responseId)
			responseInstance.addToMessages(messageInstance).save(failOnError: true, flush: true)
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: 'Fmessage'), messageInstance.id])}"
			redirect(action: "poll", params: params)
		}
	}

	def deleteMessage = {
		def ids = [params.ids].flatten()
		ids.each {id ->
			withFmessage id, {messageInstance ->
				messageInstance.toDelete()
				messageInstance.save(failOnError: true, flush: true)
			}
		}
		flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: ''), ids.size() + ' messages'])}"
		if (isAjaxRequest()) {
			render ""
		}else {
			redirect(action: params.messageSection, params: [ownerId: params.ownerId])
		}		
	}

	def archiveMessage = {
		def ids = [params.ids].flatten()
		ids.each {id ->
			withFmessage id, { messageInstance ->
				messageInstance.archive()
				messageInstance.save(failOnError: true, flush: true)
			}
		}
		flash.message = "${message(code: 'default.archived.message', args: [message(code: 'message.label', default: ''), ids.size() + ' messages'])}"
		if (request.xhr) {
			render ""
		}else {
			redirect(action: params.messageSection, params: [ownerId: params.ownerId])
		}
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

	def send = {
		def addresses = [params.addresses].flatten() - null
		def groups = [params.groups].flatten() - null
		addresses += groups.collect {Group.findByName(it).getAddresses()}.flatten()
		addresses.unique().each { address ->
			//TODO: Need to add source from app settings
			def message = new Fmessage(src: "src", dst: address, text: params.messageText)
			messageSendService.send(message)
		}
		flash.message = "Message has been queued to send to " + addresses.unique().join(", ")
		redirect (action: 'sent')
	}

	def emptyTrash = {
		Fmessage.findAllByDeleted(true)*.delete()
		redirect(action: 'inbox')
	}

	private def withFmessage(messageId= params.messageId, Closure c) {
			def m = Fmessage.get(messageId)
			if(m) c.call(m)
			else render(text: "Could not find message with id ${params.messageId}") // TODO handle error state properly
	}

	private def isAjaxRequest() {
		return request.xhr
	}

	private def getPaginationCount() {
		GrailsConfig.getConfig().pagination.max
	}
}
