package frontlinesms2

import grails.converters.JSON

class ConnectionController {
	static allowedMethods = [save: "POST", update: "POST", delete:'GET']
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
		def fconnectionInstanceList = Fconnection.list(params)
		def fconnectionInstanceTotal = Fconnection.count()

		def model = [connectionInstanceList:fconnectionInstanceList,
				fconnectionInstanceTotal:fconnectionInstanceTotal]
		if(!params.id) params.id = fconnectionInstanceList[0]?.id
		if(params.id) model << show()
		render view:'show', model:model
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

	def delete() {
		def connection = Fconnection.get(params.id)
		if(connection.status == RouteStatus.NOT_CONNECTED) {
			connection.delete()
			flash.message = message code:'connection.deleted', args:[connection.name]
			redirect action:'list'
		} else throw new RuntimeException()
	}
	
	def update = {
		remapFormParams()
		withFconnection { fconnectionInstance ->
			fconnectionInstance.properties = params
			def connectionErrors = fconnectionInstance.errors.allErrors.collect {message(code:it.codes[2], args:it.arguments.flatten())}
			if (fconnectionInstance.save()) {
			withFormat {
				html {
					flash.message = LogEntry.log(message(code: 'default.created.message', args: [message(code: 'fconnection.name'), fconnectionInstance.id]))
					redirect(controller:'connection', action: "createRoute", id: fconnectionInstance.id)
				}
				json {
					render([ok:true, redirectUrl:createLink(action:'createRoute', id:fconnectionInstance.id)] as JSON)
				}
			}
		} else {
			withFormat {
				html {
					flash.message = LogEntry.log(message(code: 'connection.creation.failed', args:[fconnectionInstance.errors]))
					redirect(controller:'connection', action:"list")
				}
				json {
					render([ok:false, text:connectionErrors.join().toString()] as JSON)
				}
			}
		}
		}
	}
	
	private def remapFormParams() {
		def cType = params.connectionType
		if(!(cType in CONNECTION_TYPE_MAP)) {
			throw new RuntimeException("Unknown connection type: " + cType)
		}
		def newParams = [:] // TODO remove this - without currently throw ConcurrentModificationException
		params.each { k, v ->
			if(k.startsWith(cType)) {
				newParams[k.substring(cType.size())] = v
			} else if(k.startsWith("_" + cType)) {
				def key = k.substring(("_" + cType).size())
				if(!params[key] && !newParams[key]) {
					newParams[key] = v as boolean
				}
			}
		}
		params << newParams
	}
	
	def createRoute = {
		CreateRouteJob.triggerNow([connectionId:params.id])
		params.createRoute = true
		flash.message = message(code: 'connection.route.connecting')
		redirect(action:'list', params:params)
	}
  
	def destroyRoute = {
		withFconnection { c ->
			fconnectionService.destroyRoutes(c)
			flash.message = message(code: 'connection.route.disconnecting')
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
			def m = messageSendService.createOutgoingMessage(params)
			messageSendService.send(m, connection)
			flash.message = LogEntry.log(message(code:'fconnection.test.message.sent'))
			redirect action:'list', id:params.id
		}
	}
	
	private def doSave(Class<Fconnection> clazz) {
		def fconnectionInstance = clazz.newInstance()
		fconnectionInstance.properties = params
		def connectionErrors = fconnectionInstance.errors.allErrors.collect {message(code:it.codes[2], args:it.arguments.flatten())}
		if (fconnectionInstance.save()) {
			withFormat {
				html {
					flash.message = LogEntry.log(message(code: 'default.created.message', args: [message(code: 'fconnection.name', default: 'Fconnection'), fconnectionInstance.id]))
					forward(action:"createRoute", id:fconnectionInstance.id)
				}
				json {
					render([ok:true, redirectUrl:createLink(action:'createRoute', id:fconnectionInstance.id)] as JSON)
				}
			}
		} else {
			withFormat {
				html {
					flash.message = LogEntry.log(message(code: 'connection.creation.failed', args:[fconnectionInstance.errors]))
					redirect(controller:'connection', action:"list")
				}
				json {
					render([ok:false, text:connectionErrors.join().toString()] as JSON)
				}
			}
		}
	}

	private def withFconnection(id = params?.id, Closure c) {
		def connection = Fconnection.get(id)
		if(connection) {
			c connection
		} else {
			flash.message = LogEntry.log(message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id]))
			redirect(controller:'connection', action:'list')
		}
	}
}

