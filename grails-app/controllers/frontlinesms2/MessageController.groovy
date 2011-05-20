package frontlinesms2

class MessageController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "inbox", params: params)
    }

    def inbox = {
		def messageInstance = Fmessage.get(params.id)
		def contactInstance
		if(messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
			contactInstance = Contact.findByAddress(messageInstance.src)
		}
		
		params.inbound = true
		[messageSection:'inbox',
				messageInstance: messageInstance,
				contactInstance: contactInstance] << list()
    }

    def sent = {
		params.inbound = false
		[messageSection:'sent'] << list()
    }

    def list = {
		params.sort = 'dateCreated'
		params.order = 'desc'
		def messageInstanceList = Fmessage.findAllByInbound(params.inbound, params)
		def model = [messageInstanceList:messageInstanceList,
				messageInstanceTotal:Fmessage.countByInbound(params.inbound)]
		model
    }
}
