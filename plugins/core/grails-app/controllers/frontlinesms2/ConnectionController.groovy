package frontlinesms2

class ConnectionController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def fconnectionService
	def messageSendService

	def index = {
		redirect(action:'create_new')
	}

	def connection_wizard = {
		def action = "save"
		def fconnectionInstance
		if(params.id) {
			fconnectionInstance = Fconnection.get(params.id)
			action = "update"
		}
		[action: action, fconnectionInstance: fconnectionInstance]
	}
	
	def save = {
		if(params.connectionType == 'email') saveEmail()
		else if(params.connectionType == 'smslib') saveSmslib()
	}
	
	def update = {
		withFconnection { fconnectionInstance ->
			if(params.receiveProtocol) params.receiveProtocol = EmailReceiveProtocol.valueOf(params.receiveProtocol.toUpperCase())
			fconnectionInstance.properties = params
			if(fconnectionInstance.save(flush:true)) {
				flash.message = "${message(code: 'default.updated.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
				redirect(controller:'connection', action: "createRoute", id: fconnectionInstance.id)
			} else {
				flash.message = "fail!  ${fconnectionInstance.errors}"
				redirect(controller:'settings', action: "connections", params: params)
			}
		}
	}
	
	def saveEmail = {
		def fconnectionInstance = new EmailFconnection()
		if(params.receiveProtocol) params.receiveProtocol = EmailReceiveProtocol.valueOf(params.receiveProtocol.toUpperCase())
		fconnectionInstance.properties = params

		if (fconnectionInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
			redirect(controller:'settings', action: "show_connections", id: fconnectionInstance.id)
		} else {
			flash.message = "fail!  ${fconnectionInstance.errors}"
			redirect(controller:'settings', action: "connections", params: params)
		}
	}

	def saveSmslib = {
		def fconnectionInstance = new SmslibFconnection()
		fconnectionInstance.properties = params
		if (fconnectionInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}"
			forward(controller:'connection', action: "createRoute", id: fconnectionInstance.id)
		} else {
			params.flashMessage = "fail!  ${fconnectionInstance.errors}"
			redirect(controller:'settings', action: "connections", params: params)
		}
	}
	
	def createRoute = {
		withFconnection { connection ->
			println "creating route for fconnection $connection"
			def link = "<br/><a href='${createLink(controller:'settings', action:'connections', id:connection.id)}'>View</a>"
			try {
				fconnectionService.createRoutes(connection)
				createSystemNotification("Created route from ${connection.camelConsumerAddress} and to ${connection.camelProducerAddress} $link")
			} catch(Exception e) {
				createSystemNotification(e.message + link)
			}
			render ""
		}
	}
  
	def destroyRoute = {
		withFconnection { c ->
			println "Destroying connection: $c"
			fconnectionService.destroyRoutes(c)
			flash.message = "Destroy route from ${c.camelConsumerAddress} and to ${c.camelProducerAddress}"
			redirect(controller:'settings', action:'connections', id:c.id)
		}
	}

	def listComPorts = {
		// This is a secret debug method for now to help devs see what ports are available
		render(text: "${serial.CommPortIdentifier.portIdentifiers*.name}")
	}

	def createTest = {
		def connectionInstance = Fconnection.get(params.id)
		[connectionInstance:connectionInstance]
	}
	
	def sendTest = {
		withFconnection { connection ->
			def message = messageSendService.getMessagesToSend(params)
			println "passing arguments ${message.class}, ${connection.class}"
			messageSendService.send(message, connection)
			flash.message = "Test message successfully sent to ${params.addresses} using ${connection.name}"
			redirect (controller:'settings', action:'show_connections', id:params.id)
		}
	}
	
	private def createSystemNotification(String text) {
		def notification = SystemNotification.findByText(text) ?: new SystemNotification(text:text)
		notification.read = false
		notification.save(failOnError:true, flush:true)
	}
	private def withFconnection(Closure c) {
		println "Fetching connection with id $params.id"
		def connection = Fconnection.get(params.id.toLong())
		println "Connection: $connection"
		if(connection) {
			c connection
		} else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}"
			redirect action:'list'
		}
	}
}
