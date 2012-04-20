package frontlinesms2

class ConnectionController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	private static final def CONNECTION_TYPE_MAP = [smslib:SmslibFconnection,
			email:EmailFconnection,
			clickatell:ClickatellFconnection,
			intellisms:IntelliSmsFconnection]

	def fconnectionService
	def messageSendService

	def index = {
		redirect(action:'create_new')
	}
	
	def list = {
		println "params are $params"
		def fconnectionInstanceList = Fconnection.list(params)
		if(!params.id) {
			params.id = Fconnection.list(params)[0]?.id
		}
		
		def fconnectionInstanceTotal = Fconnection.count()
		if(params.id) {
			render(view:'show', model:show() << [connectionInstanceList:fconnectionInstanceList,
					fconnectionInstanceTotal:fconnectionInstanceTotal])
		} else {
			flash.message = LogEntry.log("${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}")
			render(view:'show', model:[fconnectionInstanceTotal: 0])
		}
	}
	
	def show = {
		withFconnection {
			if(params.createRoute) {
				it.metaClass.getStatus = { RouteStatus.CONNECTING }
			}
			[connectionInstance: it] << [connectionInstanceList: Fconnection.list(params),
					fconnectionInstanceTotal: Fconnection.list(params)]
		}
	}

	def wizard = {
		if(params.id) {
			withFconnection {
				return [action:'update', fconnectionInstance:it]
			}
		} else {
			return [action:'save']
		}
	}
	
	def save = {
		remapFormParams()
		doSave(CONNECTION_TYPE_MAP[params.connectionType])
	}
	
	def update = {
		remapFormParams()
		withFconnection { fconnectionInstance ->
			if(params.receiveProtocol) params.receiveProtocol = EmailReceiveProtocol.valueOf(params.receiveProtocol.toUpperCase())
			fconnectionInstance.properties = params
			if(fconnectionInstance.save()) {
				flash.message = LogEntry.log("${message(code: 'default.updated.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), fconnectionInstance.id])}")
				redirect(controller:'connection', action: "createRoute", id: fconnectionInstance.id)
			} else {
				flash.message = LogEntry.log("${message(code: 'connection.creation.failed', args:[fconnectionInstance.errors])}")
				redirect(controller:'settings', action: "connections", params: params)
			}
		}
	}
	
	private def remapFormParams() {
		def cType = params.connectionType
		if(!(cType in CONNECTION_TYPE_MAP)) {
			throw new RuntimeException("${message(code: 'fconnection.unknown.type')}" + cType)
		}
		def newParams = [:] // TODO remove this - without currently throw ConcurrentModificationException
		params.each { k, v ->
			if(k.startsWith(cType)) {
				newParams[k.substring(cType.size())] = v
			}
		}
		params << newParams
	}
	
	def createRoute = {
		CreateRouteJob.triggerNow([connectionId:params.id])
		params.createRoute = true
		redirect(action:'list', params:params)
	}
  
	def destroyRoute = {
		withFconnection { c ->
			fconnectionService.destroyRoutes(c)
			flash.message = "${message(code: 'connection.route.disconnecting')}"
			redirect(action:'list', id:c.id)
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
			def message = messageSendService.createOutgoingMessage(params)
			messageSendService.send(message, connection)
			flash.message = LogEntry.log("${message(code: 'fconnection.test.message.sent')}")
			redirect (action:'list', id:params.id)
		}
	}
	
	private def doSave(Class<Fconnection> clazz) {
		def fconnectionInstance = clazz.newInstance()
		fconnectionInstance.properties = params
		if (fconnectionInstance.save()) {
			flash.message = LogEntry.log("${message(code: 'default.created.message', args: [message(code: 'fconnection.name', default: 'Fconnection'), fconnectionInstance.id])}")
			forward(controller:'connection', action:"createRoute", id:fconnectionInstance.id)
		} else {
			flash.message = LogEntry.log("${message(code: 'connection.creation.failed', args:[fconnectionInstance.errors])}")
			redirect(controller:'connection', action:"list")
		}
	}
	
	private def withFconnection(id = params?.id, Closure c) {
		def connection = Fconnection.get(id)
		if(connection) {
			c connection
		} else {
			flash.message = LogEntry.log("${message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])}")
			redirect(controller:'connection', action:'list')
		}
	}
}
