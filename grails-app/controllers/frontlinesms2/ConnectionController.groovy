package frontlinesms2

class ConnectionController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def fconnectionService
	def messageSendService

	def index = {
		redirect(action:'list')
	}

	def list = {
		def fconnectionInstanceList = Fconnection.list(params)
		if(!params.id) {
			params.id = Fconnection.list(params)[0]?.id
		}		
		def connectionInstance = Fconnection.get(params.id)
		def fconnectionInstanceTotal = Fconnection.count()
		if(params.id){
			render(view:'show', model:show() << [connectionInstanceList: fconnectionInstanceList,
				connectionInstance: connectionInstance,
				fconnectionInstanceTotal: fconnectionInstanceTotal])
		} else {
			[settingsSection:'connections',
				connectionInstanceList: fconnectionInstanceList,
				connectionInstance: connectionInstance,
				fconnectionInstanceTotal: fconnectionInstanceTotal]
		}
	}

	def create = {}

	def createEmail = {
		if(params.flashMessage) { flash.message = params.flashMessage }
		def fconnectionInstance = new EmailFconnection()
		fconnectionInstance.properties = params
		[settingsSection:'connections', fconnectionInstance: fconnectionInstance]
	}

	def createSmslib = {
		if(params.flashMessage) { flash.message = params.flashMessage }
		def fconnectionInstance = new SmslibFconnection()
		fconnectionInstance.properties = params
		[settingsSection:'connections', fconnectionInstance: fconnectionInstance]
	}

	def show = {
		withFconnection {
			[connectionInstance: it] << [settingsSection:'connections', connectionInstanceList: Fconnection.list(params), fconnectionInstanceTotal: Fconnection.list(params)]
		}
	}

	def saveEmail = {
		def fconnectionInstance = new EmailFconnection()
		if(params.receiveProtocol) params.receiveProtocol = EmailReceiveProtocol.valueOf(params.receiveProtocol.toUpperCase())
		fconnectionInstance.properties = params

		if (fconnectionInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
			redirect(action: "list", id: fconnectionInstance.id)
		} else {
			params.flashMessage = "fail!  ${fconnectionInstance.errors}"
			redirect(action: "createEmail", params: params)
		}
	}

	def saveSmslib = {
		def fconnectionInstance = new SmslibFconnection()
		fconnectionInstance.properties = params

		if (fconnectionInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
			redirect(action: "list", id: fconnectionInstance.id)
		} else {
			params.flashMessage = "fail!  ${fconnectionInstance.errors}"
			redirect(action: "createSmslib", params: params)
		}
	}

	def createRoute = {
		withFconnection { settings ->
			fconnectionService.createRoute(settings)
			flash.message = "Created route from ${settings.camelConsumerAddress} and to ${settings.camelProducerAddress}"
			redirect action:'list', id:settings.id
		}
	}

	def createTest = {
		def connectionInstance = Fconnection.get(params.id)
		[connectionInstance:connectionInstance]
	}

	def sendTest = {
		println params

		withFconnection {
			flash.message = "Test message successfully sent to ${it.name}"
			messageSendService.dispatch(new Fmessage(src:"$it", dst: params.number, text: params.message), it)
			redirect (action:'show', id:params.id)
		}
	}
	
	private def withFconnection(Closure c) {
		def connection = Fconnection.get(params.id)
		if(connection) {
			c connection
		} else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
			redirect action:'list'
		}
	}
}
