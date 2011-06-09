package frontlinesms2

class MessageController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def index = {
		redirect(action: "inbox", params: params)
	}

	def show = {
		def messageInstance = Fmessage.get(params.id)
		def pollInstance = Poll.get(params.pollId)
		def folderInstance = Poll.get(params.folderId)
		def contactInstance
		messageInstance.updateDisplaySrc()
		if(!messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		
		if(params.pollId){
			params.messageSection = 'poll'
		}
		render view:params.messageSection,
				model:[messageInstance: messageInstance,
						contactInstance: contactInstance,
						folderInstanceList: Folder.findAll(),
						pollInstanceList: Poll.findAll(),
						folderInstance: folderInstance,
						pollInstance: pollInstance] << list()
	}

    def inbox = {
		def messageInstanceList = Fmessage.getInboxMessages()
		def latestMessage
		messageInstanceList.each {
			if(!latestMessage) {
				latestMessage = it
			} else{
				if(it.dateCreated.compareTo(latestMessage.dateCreated) < 0) {
					latestMessage = it
				}
			}
		}
		params.id = latestMessage?.id
		if(params.id) {
			redirect(action:'show', params:params)
		} else {
			[messageSection: 'inbox',
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll()]
		}
    }

    def sent = {
		params.inbound = false
		[messageSection:'sent'] << list()
    }

	def poll = {
		
		def pollInstance = Poll.get(params.pollId)
		def messageInstanceList = pollInstance.messages
		def latestMessage
		messageInstanceList.each {
			if(!latestMessage) {
				latestMessage = it
			} else{
				if(it.dateCreated.compareTo(latestMessage.dateCreated) < 0) {
					latestMessage = it
				}
			}
		}
		println "pollInstance.messages.size: ${pollInstance.messages.size()}"
		params.id = latestMessage?.id
		if(params.id) {
			redirect(action:'show', params:params)
		} else {
			[pollInstance: pollInstance,
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll()]
		}
	}
	
	def folder = {
		def folderInstance = Folder.get(params.folderId)
		def messageInstanceList = folderInstance.messages
		def latestMessage
		messageInstanceList.each {
			if(!latestMessage) {
				latestMessage = it
			} else{
				if(it.dateCreated.compareTo(latestMessage.dateCreated) < 0) {
					latestMessage = it
				}
			}
		}
		params.id = latestMessage?.id
		if(params.id) {
			redirect(action:'show', params:params)
		} else {
			[folderInstance: folderInstance,
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll()]
		}
	}
	
	def list = {
		def messageInstanceList
		if(params.messageSection == 'inbox') {
			messageInstanceList = Fmessage.getInboxMessages().each { it.updateDisplaySrc()}
			[messageInstanceList: messageInstanceList,
				messageSection: 'inbox',
				messageInstanceTotal: Fmessage.getInboxMessages().size()]
		} else if(params.messageSection == 'poll') {
			def pollInstance = Poll.get(params.pollId)
		 	messageInstanceList = pollInstance.messages
			messageInstanceList.each{ it.updateDisplaySrc() }
			[messageInstanceList: messageInstanceList,
					messageSection: 'poll',
					messageInstanceTotal: pollInstance.messages.size(),
					pollInstance: pollInstance,
					pollInstanceList: Poll.findAll(),
					responseList: pollInstance.responses]
		}else if(params.messageSection == 'folder') {
			def folderInstance = Folder.get(params.folderId)
		 	messageInstanceList = folderInstance.messages
			messageInstanceList.each{ it.updateDisplaySrc() }
			[messageInstanceList: messageInstanceList,
					messageSection: 'folder',
					messageInstanceTotal: folderInstance.messages.size(),
					folderInstance: folderInstance,
					folderInstanceList: Folder.findAll(),
					pollInstanceList: Poll.findAll()]
		}else {
			[folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll()]
		}
		
	}
	def move = {
		def messageOwner
		if(params.pollId){
			messageOwner = Poll.get(params.pollId)
		}else{
			messageOwner = Folder.get(params.folderId)
		}
		def messageInstance = Fmessage.get(params.id)
		if(messageOwner instanceof Poll){
			def unknownResponse = messageOwner.getResponses().find { it.value == 'Unknown'}
			unknownResponse.addToMessages(Fmessage.get(params.id)).save(failOnError: true, flush: true)
		}else{
			messageOwner.addToMessages(Fmessage.get(params.id)).save(failOnError: true, flush: true)
		}
		
		redirect(action: "show", params: params)
	}

	def changeResponse = {
		def pollInstance = Poll.get(params.pollId)
		def responseInstance = PollResponse.get(params.responseId)
		def messageInstance = Fmessage.get(params.id)
		responseInstance.addToMessages(messageInstance).save(failOnError: true, flush: true)
		redirect(action: "show", params: params)
	}
	
	def deleteMessage = {
		def messageInstance = Fmessage.get(params.id)
		messageInstance.toDelete()
		messageInstance.save(failOnError: true, flush: true)
		Fmessage.get(params.id).messageOwner?.refresh()
		redirect(action: params.messageSection, params:params)
	}
}
