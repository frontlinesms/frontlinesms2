package frontlinesms2

class MessageController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def index = {
		redirect(action: "inbox", params: params)
	}

	def show = {
		def messageInstance = Fmessage.get(params.id)
		def pollInstance = Poll.get(params.pollId)
		def contactInstance
		if(!messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}

		if(messageInstance) {
			contactInstance = Contact.findByAddress(messageInstance.src)
		}

		def model = [messageInstance: messageInstance,
						contactInstance: contactInstance,
						pollInstanceList: Poll.findAll(),
						pollInstance: pollInstance] << "${params.messageSection}"()

		render view:params.messageSection, model:model
	}

    def inbox = {
		params.sort = 'dateCreated'
		params.order = 'desc'
		params.inbound = true
		def messageInstanceList = Fmessage.getInboxMessages()
		messageInstanceList.each {
			it.updateDisplaySrc()
		}
		[messageSection:'inbox',
			messageInstanceList: messageInstanceList,
			messageInstanceTotal: Fmessage.getInboxMessages().size()] << list()
    }

    def sent = {
		params.inbound = false
		[messageSection:'sent'] << list()
    }

	def poll = {
		def pollInstance = Poll.get(params.pollId)
		def messageInstanceList = pollInstance.messages
		messageInstanceList.each {
			it.updateDisplaySrc()
		}
		[messageSection:'poll',
				messageInstanceList: messageInstanceList,
				messageInstanceTotal: pollInstance.messages.size(),
				pollInstanceList: Poll.findAll(),
				pollInstance: pollInstance,
				responseList: pollInstance.getResponses()]
	}

	def list = {
		[pollInstanceList: Poll.findAll()]
	}
	
	def move = {
		def pollInstance = Poll.get(params.pollId)
		def messageInstance = Fmessage.get(params.id)
		def unknownResponse = pollInstance.getResponses().find { it.value == 'Unknown'}
		unknownResponse.addToMessages(Fmessage.get(params.id)).save(failOnError: true, flush: true)
		redirect(action: "show", params: params)
	}

	def changeResponse = {
		def pollInstance = Poll.get(params.pollId)
		def responseInstance = PollResponse.get(params.responseId)
		def messageInstance = Fmessage.get(params.id)
		responseInstance.addToMessages(messageInstance).save(failOnError: true, flush: true)
		redirect(action: "show", params: params)
	}
}
