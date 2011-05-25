package frontlinesms2

class ConnectionController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def fconnectionService

	def index = {
		redirect(action:'list')
	}

	def list = {
		def fconnectionInstanceList = Fconnection.list(params)
		def connectionInstance = Fconnection.get(params.id)
		def fconnectionInstanceTotal = Fconnection.count()

		[connectionInstanceList: fconnectionInstanceList,
				connectionInstance: connectionInstance,
				fconnectionInstanceTotal: fconnectionInstanceTotal]
	}

	def create = {}

	def createEmail = {
		def fconnectionInstance = new EmailFconnection()
		fconnectionInstance.properties = params
		[fconnectionInstance: fconnectionInstance]
	}

	def createSmslib = {
		def fconnectionInstance = new SmslibFconnection()
		fconnectionInstance.properties = params
		[fconnectionInstance: fconnectionInstance]
	}

	def show = {
		withFconnection {
			[connectionInstance: it] << list()
		}
	}

	def saveEmail = {
		def fconnectionInstance = new EmailFconnection()
		if(params.protocol) params.protocol = EmailProtocol.valueOf(params.protocol.toUpperCase())
		fconnectionInstance.properties = params

		if (fconnectionInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
			redirect(action: "list", id: fconnectionInstance.id)
		} else {
			render "fail!  ${fconnectionInstance.errors}"
		}
	}

	def saveSmslib = {
		def fconnectionInstance = new SmslibFconnection()
		fconnectionInstance.properties = params

		if (fconnectionInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
			redirect(action: "list", id: fconnectionInstance.id)
		} else {
			render "fail!  ${fconnectionInstance.errors}"
		}
	}

	def createRoute = {
		withFconnection { settings ->
			fconnectionService.createRoute(settings)
			flash.message = "Created route from ${settings.camelAddress()}"
			redirect action:'list', id:settings.id
		}
	}
	
	private def withFconnection(Closure c) {
		def connection = Fconnection.get(params.id)
		if(connection) c connection
		else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
			redirect action:'list'
		}
	}
}
