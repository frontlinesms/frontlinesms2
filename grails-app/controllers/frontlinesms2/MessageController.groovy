package frontlinesms2

class MessageController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "inbox", params: params)
    }

    def inbox = {
		def messageInstance = Fmessage.get(params.id)
		return [messageSection:'inbox',
				messageInstance: messageInstance] << list()
    }

    def sent = {
		[messageSection:'sent'] << list()
    }

    def list = {
		[messageInstanceList:Fmessage.list(params),
				messageInstanceTotal:Fmessage.count()]
    }
}
