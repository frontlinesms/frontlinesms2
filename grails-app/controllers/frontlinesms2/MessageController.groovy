package frontlinesms2

import grails.util.GrailsConfig
import grails.converters.JSON

class MessageController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def messageSendService
	
	def index = {
		redirect(action:'inbox')
	}

	def show = { messageInstanceList ->
		def messageInstance = params.messageId ? Fmessage.get(params.messageId) : messageInstanceList[0]
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		[messageInstance: messageInstance,
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll(),
				messageCount: Fmessage.countAllMessages()]
	}

	def trash = {
		def max = params.max ?: GrailsConfig.getConfig().pagination.max
		def offset = params.offset ?: 0
		def isStarred = params['starred']
		def messageInstanceList = Fmessage.getDeletedMessages(isStarred, max, offset)
		messageInstanceList.each { it.updateDisplaySrc()}
			[messageInstanceList: messageInstanceList,
					messageSection: 'trash',
					messageInstanceTotal: Fmessage.countDeletedMessages(isStarred)] << show(messageInstanceList)
	}

	def inbox = {
		def max = params.max ?: GrailsConfig.getConfig().pagination.max
		def offset = params.offset ?: 0
		def isStarred = params['starred']
		def messageInstanceList = Fmessage.getInboxMessages(isStarred, max, offset)
		messageInstanceList.each { it.updateDisplaySrc()}
			[messageInstanceList: messageInstanceList,
					messageSection: 'inbox',
					messageInstanceTotal: Fmessage.countInboxMessages(isStarred)] << show(messageInstanceList)
	}

	def sent = {
		def max = params.max ?: GrailsConfig.getConfig().pagination.max
		def offset = params.offset ?: 0
		def isStarred = params['starred']
		def messageInstanceList = Fmessage.getSentMessages(isStarred, max, offset)
		messageInstanceList.each { it.updateDisplaySrc()}
		[messageSection:'sent',
				messageInstanceList:messageInstanceList,
				messageInstanceTotal: Fmessage.countSentMessages(isStarred)] << show(messageInstanceList)
	}

	def pending = {
		def max = params.max ?: GrailsConfig.getConfig().pagination.max
		def offset = params.offset ?: 0
		def isStarred = params['starred']
		def messageInstanceList = Fmessage.getPendingMessages(isStarred, max, offset)
		messageInstanceList.each { it.updateDisplaySrc() }
		[messageInstanceList: messageInstanceList,
				messageSection: 'pending',
				messageInstanceTotal: Fmessage.countPendingMessages(isStarred)] << show(messageInstanceList)
	}

	def poll = {
		def max = params.max ?: GrailsConfig.getConfig().pagination.max
		def offset = params.offset ?: 0
		def ownerInstance = Poll.get(params.ownerId)
		def isStarred = params['starred']
		def messageInstanceList = ownerInstance.getMessages(isStarred, max, offset)
		messageInstanceList.each { it.updateDisplaySrc() }
		
		params.messageSection = 'poll'
		[messageInstanceList: messageInstanceList,
				messageSection: 'poll',
				messageInstanceTotal: ownerInstance.countMessages(isStarred),
				ownerInstance: ownerInstance,
				responseList: ownerInstance.responseStats,
				pollResponse: ownerInstance.responseStats as JSON] << show(messageInstanceList)
	}
	
	def folder = {
		def max = params.max ?: GrailsConfig.getConfig().pagination.max
		def offset = params.offset ?: 0
		def folderInstance = Folder.get(params.ownerId)
		def isStarred = params['starred']
		def messageInstanceList = folderInstance.getFolderMessages(isStarred, max, offset)
		messageInstanceList.each{ it.updateDisplaySrc() }

		if(params.flashMessage) { flash.message = params.flashMessage }

		params.messageSection = 'folder'
		[messageInstanceList: messageInstanceList,
				messageSection: 'folder',
				messageInstanceTotal: folderInstance.countMessages(isStarred),
				ownerInstance: folderInstance] << show(messageInstanceList)
	}

	def move = {
		withFmessage { messageInstance ->
			def messageOwner
			if(params.messageSection == 'poll') {
				messageOwner = Poll.get(params.ownerId)
			} else if (params.messageSection == 'folder') {
				messageOwner = Folder.get(params.ownerId)
			}
			if(messageOwner instanceof Poll) {
				def unknownResponse = messageOwner.getResponses().find { it.value == 'Unknown'}
				unknownResponse.addToMessages(Fmessage.get(params.messageId)).save(failOnError: true, flush: true)
			} else if (messageOwner instanceof Folder) {
				messageOwner.addToMessages(Fmessage.get(params.messageId)).save(failOnError: true, flush: true)
			}
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: 'Fmessage'), messageInstance.id])}"
			redirect(action: params.messageSection, params: params)
		}
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
		withFmessage { messageInstance ->
			messageInstance.toDelete()
			messageInstance.save(failOnError: true, flush: true)
			Fmessage.get(params.messageId).messageOwner?.refresh()
			flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: 'Fmessage'), messageInstance.id])}"
			params.remove('messageId')
			redirect(action: params.messageSection, params:params)
		}
	}
	
	def changeStarStatus = {
		withFmessage { messageInstance ->
			messageInstance.starred ? messageInstance.removeStar() : messageInstance.addStar()
			messageInstance.save(failOnError: true, flush: true)
			Fmessage.get(params.messageId).messageOwner?.refresh()
            params.remove('messageId')
			render(text: messageInstance.starred ? "starred" : "")
		}
	}

	def send = {
		def addresses = [params.addresses].flatten() - null
		def groups = [params.groups].flatten() - null
		addresses += groups.collect {Group.findByName(it).getAddresses()}.flatten()
		addresses.unique().each { address ->
			//TODO: Need to add source from app settings
			def message = new Fmessage(dst: address, text: params.messageText)
			messageSendService.process(message)
			message.save(failOnError: true, flush: true)
		}
		flash.message = "Message has been queued to send to " + addresses.unique().join(", ")
		redirect (action: 'sent')
	}

	def emptyTrash = {
		Fmessage.findAllByDeleted(true)*.delete()
		redirect(action: 'inbox')
	}

	private def withFmessage(Closure c) {
		def m = Fmessage.get(params.messageId)
		if(m) c.call(m)
		else render(text: "Could not find message with id ${params.messageId}") // TODO handle error state properly
	}
}
