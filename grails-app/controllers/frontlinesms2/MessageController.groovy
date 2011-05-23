package frontlinesms2

class MessageController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def index = {
		redirect(action: "inbox", params: params)
	}

	def show = {
		def messageInstance = Fmessage.get(params.id)
		def contactInstance
		if(params.messageSection=='inbox' && messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
			contactInstance = Contact.findByAddress(messageInstance.src)
		}

		render view:params.messageSection,
				model:[messageInstance: messageInstance,
						contactInstance: contactInstance] << "${params.messageSection}"()
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
		
		[messageInstanceList: pollInstance.messages,
				messageInstanceTotal: pollInstance.messages.size(),
				pollInstanceList: Poll.findAll(),
				pollInstance: pollInstance,
				pollResponseList: pollInstance.responses] // FIXME should add messageSection:'poll'
	}

    def list = {
		params.sort = 'dateCreated'
		params.order = 'desc'
		def messageInstanceList = Fmessage.findAllByInbound(params.inbound, params)
		[messageInstanceList:messageInstanceList,
				messageInstanceTotal:Fmessage.countByInbound(params.inbound),
				pollInstanceList: Poll.findAll()]
    }
}
