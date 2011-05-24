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
		if(params.messageSection=='inbox' && messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
			contactInstance = Contact.findByAddress(messageInstance.src)
		}

		render view:params.messageSection,
				model:[messageInstance: messageInstance,
						contactInstance: contactInstance,
						pollInstanceList: Poll.findAll(),
						pollInstance: pollInstance] << "${params.messageSection}"()
	}

    def inbox = {		
		params.inbound = true
		[messageSection:'inbox'] << list()
    }

    def sent = {
		params.inbound = false
		[messageSection:'sent'] << list()
    }

	def poll = {
		def pollInstance = Poll.get(params.pollId)
		
		[messageSection:'poll',
				messageInstanceList: pollInstance.messages,
				messageInstanceTotal: pollInstance.messages.size(),
				pollInstanceList: Poll.findAll(),
				pollInstance: pollInstance,
				pollResponseList: pollInstance.responses]
	}

    def list = {
		params.sort = 'dateCreated'
		params.order = 'desc'
		def messageInstanceList = Fmessage.findAllByInbound(params.inbound, params)
		[messageInstanceList:messageInstanceList,
				messageInstanceTotal:Fmessage.countByInbound(params.inbound),
				pollInstanceList: Poll.findAll()]
    }
	
	def move = {
		def pollInstance = Poll.get(params.pollId)
		def messageInstance = Fmessage.get(params.id)
		def pollInstanceList = Poll.findAll()
		
		pollInstanceList.each{ poll ->
			messageToBeDeleted = poll.getMessages().find{ messageInstance }
			if(messageToBeDeleted) {
				poll.responses.removeAll(messageToBeDeleted)
				println "Message Deleted"
			}
		}
		println "Message Deleted"
		pollInstance.responses.toArray()[0].addToMessages(messageInstance)
		redirect(action: "poll", params: params)
	}
}
