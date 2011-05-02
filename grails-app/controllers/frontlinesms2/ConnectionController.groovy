package frontlinesms2

class ConnectionController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def fconnectionService

	def index = {
		redirect(action:'list')
	}

    def list = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		def fconnectionInstanceList = Fconnection.list(params)
		def connectionInstance = Fconnection.get(params.id)
		def fconnectionInstanceTotal = Fconnection.count()

		return [connectionInstanceList: fconnectionInstanceList, connectionInstance: connectionInstance, fconnectionInstanceTotal: fconnectionInstanceTotal]
	}

	def create = {}

	def createEmail = {
		def fconnectionInstance = new EmailFconnection()
		fconnectionInstance.properties = params
		return [fconnectionInstance: fconnectionInstance]
	}

	def createSmslib = {
		def fconnectionInstance = new SmslibFconnection()
		fconnectionInstance.properties = params
		return [fconnectionInstance: fconnectionInstance]
	}

	def show = {
		def connectionInstance = Fconnection.get(params.id)
        if (!connectionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Connection'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [connectionInstance: connectionInstance] << list()
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
		println "Executiung creatRoute closure with params: ${params}"
		withFconnection { settings ->
			fconnectionService.createRoute(settings)
			flash.message = "Created route from ${settings.camelAddress()}"
			redirect action:'list'
		}
	}
	def withFconnection(id=params.id, Closure c) {
		println "withFconnection id=${id}"
		def connection = Fconnection.get(id)
		if(connection) c.call connection
		else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), id])}"
			redirect action:'list'
		}
	}
}
