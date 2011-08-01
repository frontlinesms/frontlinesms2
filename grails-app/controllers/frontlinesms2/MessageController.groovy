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
		if(params.checkedId && params.checkedId != params.messageId) {
			params.remove('checkedId')
			redirect(action:params.action)
		}
		
		def messageInstance = params.messageId ? Fmessage.get(params.messageId) : messageInstanceList ? messageInstanceList[0]:null
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		def responseInstance
		if (messageInstance?.messageOwner) { responseInstance = messageInstance.messageOwner }
		
		[messageInstance: messageInstance,
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll(),
				responseInstance: responseInstance,
				radioShows: RadioShow.findAll(),
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
		def messageInstanceList = folderInstance?.getFolderMessages(isStarred, max, offset)
		messageInstanceList.each{ it.updateDisplaySrc() }

		if(params.flashMessage) { flash.message = params.flashMessage }

		params.messageSection = 'folder'
		[messageInstanceList: messageInstanceList,
				messageSection: 'folder',
				messageInstanceTotal: folderInstance.countMessages(isStarred),
				ownerInstance: folderInstance] << show(messageInstanceList)
	}

	def radioShow = {
		def max = params.max ?: GrailsConfig.getConfig().pagination.max
		def offset = params.offset ?: 0
		def showInstance = RadioShow.get(params.ownerId)
		def isStarred = params['starred']
		def messageInstanceList = showInstance?.getShowMessages(isStarred, max, offset)
		messageInstanceList.each{ it.updateDisplaySrc() }

		params.messageSection = 'radioShow'
		[messageInstanceList: messageInstanceList,
				messageSection: 'radioShow',
				messageInstanceTotal: showInstance.countMessages(isStarred),
				ownerInstance: showInstance] << show(messageInstanceList)
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
				unknownResponse.addToMessages(Fmessage.get(params.messageId) ?: messageInstance).save(failOnError: true, flush: true)
			} else if (messageOwner instanceof Folder) {
				messageOwner.addToMessages(Fmessage.get(params.messageId) ?: messageInstance).save(failOnError: true, flush: true)
			}

			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: 'Fmessage'), messageInstance.id])}"
			
		}
		if(params.count) {
			def messageCount = params.count
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: ''),messageCount +' messages'])}"
			params.remove('count')
		}
		params.remove('checkedMessageIdList')
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
		withFmessage { messageInstance ->
			messageInstance.toDelete()
			messageInstance.save(failOnError: true, flush: true)
			if(params.messageId) {
				Fmessage.get(params.messageId).messageOwner?.refresh()
				params.remove('messageId')
				flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: 'Fmessage'), messageInstance.id])}"
			} else {
				Fmessage.get(messageInstance.id).messageOwner?.refresh()
			}
		}
		if(params.count) {
			def messageCount = params.count
			flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: ''),messageCount +' messages'])}"
			params.remove('count')
		}
		if(params.checkedMessageIdList){
			params.remove('checkedMessageIdList')			
			render ""
		}else {
			redirect(action: params.messageSection, params: params)
		}		
	}

    def archiveMessage = {
		withFmessage { messageInstance ->
			messageInstance.archive()
			messageInstance.save(failOnError: true, flush: true)
			flash.message = "${message(code: 'default.archived.message', args: [message(code: 'message.label', default: 'Fmessage'), messageInstance.id])}"
			params.remove('messageId')
			params.remove('checkedId')
			Fmessage.get(messageInstance.id).refresh()
		}
		
		if(params.count) {
				def messageCount = params.count
				flash.message = "${message(code: 'default.archived.message', args: [message(code: 'message.label', default: ''),messageCount +' messages'])}"
				params.remove('count')
		}

		if(params.checkedMessageIdList){
			params.remove('checkedMessageIdList')			
			render ""
		}else {
			redirect(action: params.messageSection, params: params)
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
			def message = new Fmessage(dst: address, text: params.messageText)
			messageSendService.send(message)
		}
		flash.message = "Message has been queued to send to " + addresses.unique().join(", ")
		redirect (action: 'sent')
	}

	def emptyTrash = {
		Fmessage.findAllByDeleted(true)*.delete()
		redirect(action: 'inbox')
	}

	private def withFmessage(Closure c) {
		if(params.checkedMessageIdList) { // FIXME surely this should be explicitly handled in a different closure - this is potentially very misleading given the name of the method
			params.remove('messageId')
			getCheckedMessageList().each{ m ->
				if(m) c.call(m)
			}
		} // FIXME should there be an 'else' before this next clause, or do we really want this to happen twice in some cases?
		if(params.messageId) {
			def m = Fmessage.get(params.messageId)
			if(m) c.call(m)
			else render(text: "Could not find message with id ${params.messageId}") // TODO handle error state properly
		}
		
	}
	
	private def getCheckedMessageList() {
		def messageList = []
		def checkedMessageIdList = params.checkedMessageIdList.tokenize(',').unique();
		checkedMessageIdList.each { id ->
			messageList << Fmessage.get(id)
		}
		params.count = messageList.size
		messageList	
	}
}
