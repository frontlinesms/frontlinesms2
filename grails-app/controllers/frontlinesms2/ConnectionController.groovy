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

	def createX = {
		def fconnectionInstance = new Fconnection()
		fconnectionInstance.properties = params
		return [fconnectionInstance: fconnectionInstance]
	}

	def createEmail = {
		createX
	}

	def createSmslib = {
		createX
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
		def fconnectionInstance = new Fconnection()
		fconnectionInstance.properties = params
		fconnectionInstance.type = 'Email'

		if (fconnectionInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
			redirect(action: "list", id: fconnectionInstance.id)
		} else {
			render "fail!  ${fconnectionInstance.errors}"
		}
	}

	def saveSmslib = {
		def fconnectionInstance = new Fconnection()
		fconnectionInstance.properties = params
		fconnectionInstance.type = 'Phone/Modem'

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
			flash.message = "Created route from ${settings.camelAddress}"
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
