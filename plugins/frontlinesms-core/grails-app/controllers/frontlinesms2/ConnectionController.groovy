package frontlinesms2

import grails.converters.JSON
class ConnectionController extends ControllerUtils {
	static allowedMethods = [save: "POST", update: "POST", delete:'GET']

	def fconnectionService
	def messageSendService
	def smssyncService

	def index() {
		redirect action:'list'
	}
	
	def list() {
		def fconnectionInstanceList = Fconnection.list(params)
		def fconnectionInstanceTotal = Fconnection.count()

		def model = [connectionInstanceList:fconnectionInstanceList,
				fconnectionInstanceTotal:fconnectionInstanceTotal]
		if(params?.id) {
			model << show()
			render view:'show', model:model
		} else {
			params.id = fconnectionInstanceList[0]?.id
			render view:'show', model:model
		}
	}
	
	def show() {
		withFconnection {
			[connectionInstance: it] << [connectionInstanceList: Fconnection.list(params),
					fconnectionInstanceTotal: Fconnection.list(params)]
		}
	}

	def wizard() {
		if(params.id) {
			withFconnection {
				return [action:'update', fconnectionInstance:it]
			}
		} else {
			return [action:'save']
		}
	}
	
	def save() {
		remapFormParams()
		doSave(Fconnection.implementations.find { it.shortName == params.connectionType })
	}

	def delete() {
		def connection = Fconnection.get(params.id)
		if(connection.status == ConnectionStatus.DISABLED) {
			connection.delete()
			flash.message = message code:'connection.deleted', args:[connection.name]
			redirect action:'list'
		} else throw new RuntimeException()
	}
	
	def update() {
		remapFormParams()
		withFconnection { fconnectionInstance ->
			fconnectionInstance.properties = params
			fconnectionInstance.validate()
			def connectionErrors = fconnectionInstance.errors.allErrors.collect { message(error:it) }
			if (fconnectionInstance.save()) {
			withFormat {
				html {
					flash.message = LogEntry.log(message(code: 'default.created.message', args: [message(code: 'fconnection.name'), fconnectionInstance.id]))
					redirect(controller:'connection', action: 'enable', id: fconnectionInstance.id)
				}
				json {
					render([ok:true, redirectUrl:createLink(action:'enable', id:fconnectionInstance.id)] as JSON)
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
		if(!(cType in Fconnection.implementations*.shortName)) {
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
	
	def enable() {
		EnableFconnectionJob.triggerNow([connectionId:params.id])
		params.connecting = true
		flash.message = message(code: 'connection.route.connecting')
		def connectionInstance = Fconnection.get(params.id)
		if(connectionInstance?.shortName == 'smssync')
			smssyncService.startTimeoutCounter(connectionInstance)
		redirect(action:'list', params:params)
	}
  
	def disable() {
		withFconnection { c ->
			fconnectionService.disableFconnection(c)
			flash.message = message(code: 'connection.route.disconnecting')
			redirect(action:'list', id:c.id)
		}
	}

	def listComPorts() {
		// This is a secret debug method for now to help devs see what ports are available
		render text:serial.CommPortIdentifier.portIdentifiers*.name
	}

	def createTest() {
		def connectionInstance = Fconnection.get(params.id)
		[connectionInstance:connectionInstance]
	}
	
	def sendTest() {
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
		fconnectionInstance.validate()
		println fconnectionInstance.errors.allErrors
		def connectionErrors = fconnectionInstance.errors.allErrors.collect { message(error:it) }
		if (fconnectionInstance.save()) {
			withFormat {
				html {
					flash.message = LogEntry.log(message(code: 'default.created.message', args: [message(code: 'fconnection.name', default: 'Fconnection'), fconnectionInstance.id]))
					forward action:'enable', id:fconnectionInstance.id
				}
				json {
					render([ok:true, redirectUrl:createLink(action:'enable', id:fconnectionInstance.id)] as JSON)
				}
			}
		} else {
			withFormat {
				html {
					flash.message = LogEntry.log(message(code: 'connection.creation.failed', args:[fconnectionInstance.errors]))
					redirect(controller:'connection', action:"list")
				}
				json {
					render([ok:false, text:connectionErrors.unique().join(", ").toString()] as JSON)
				}
			}
		}
	}

	private def withFconnection = withDomainObject Fconnection, { params.id }, { redirect(controller:'connection', action:'list') }
}

