package frontlinesms2

class ConnectionController {

    def index = {
		def fconnectionInstanceList = Fconnection.findAll()
		def connectionInstance = Fconnection.get(params.id)

		return [connectionInstanceList: fconnectionInstanceList, connectionInstance: connectionInstance]
	}

	def create = {}

	def createEmail = {
		def fconnectionInstance = new Fconnection()
		fconnectionInstance.properties = params
		return [fconnectionInstance: fconnectionInstance]
	}

	def saveEmail = {
		def fconnectionInstance = new Fconnection()
		fconnectionInstance.properties = params
		fconnectionInstance.type = 'Email'

		if (fconnectionInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
			redirect(action: "index", id: fconnectionInstance.id)
		} else {
			render "fail!  ${fconnectionInstance.errors}"
		}
	}
}
