package frontlinesms2

import grails.converters.JSON
class ConnectionController extends ControllerUtils {
	static allowedMethods = [save: "POST", update: "POST", delete:'GET']
	static final String RULE_PREFIX = "fconnection-"

	def fconnectionService
	def messageSendService
	def smssyncService
	def appSettingsService
	def grailsApplication

	def index() {
		redirect action:'list'
	}

	def list() {
		def fconnectionInstanceList = Fconnection.list(params)
		def fconnectionInstanceTotal = Fconnection.count()
		def appSettings = [:]
		appSettings['routing.otherwise'] = appSettingsService.get("routing.otherwise")
		appSettings['routing.use'] = appSettingsService.get("routing.use")
		def fconnectionRoutingMap = getRoutingRules(appSettings['routing.use'])

		def model = [:]
		withFconnection {
			model << [connectionInstance: it]
		}
		[connectionInstanceList:fconnectionInstanceList,
				fconnectionInstanceTotal:fconnectionInstanceTotal,
				fconnectionRoutingMap:fconnectionRoutingMap,
				appSettings:appSettings]
	}

	def wizard() {
		if(params.id) {
			withFconnection {
				if(it.userMutable) {
					return [action:'update', fconnectionInstance:it]
				}
			}
		} else {
			return [action:'save']
		}
	}

	def save() {
		remapFormParams()
		doSave(Fconnection.getImplementations(params).find { it.shortName == params.connectionType })
	}

	def delete() {
		def connection = Fconnection.get(params.id)
		if(connection.status == ConnectionStatus.DISABLED) {
			connection.delete()
			flash.message = message code:'connection.deleted', args:[connection.name]
		} else { 
			throw new RuntimeException()
		}
		redirect action:'list'
	}

	def update() {
		remapFormParams()
		withFconnection { fconnectionInstance ->
			fconnectionInstance.properties = params
			fconnectionInstance.validate()
			def connectionErrors = fconnectionInstance.errors.allErrors.collect { message(error:it) }
			if(fconnectionInstance.save()) {
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

	def changeRoutingPreferences() {
		appSettingsService.set('routing.use', params.routingUseOrder)
		redirect action:'list'
	}

	private getRoutingRules(routingRules) {
		def fconnectionRoutingList = []
		def fconnectionRoutingMap = [:]
		def connectionInstanceList = Fconnection.findAllBySendEnabled(true)

		if(routingRules) {
			fconnectionRoutingList = routingRules.split(/\s*,\s*/)

			// Replacing fconnection rules with fconnection instances
			fconnectionRoutingList = fconnectionRoutingList.collect { rule ->
				if(rule.startsWith(RULE_PREFIX)) {
					connectionInstanceList.find {
						it.id == ((rule - RULE_PREFIX) as Integer)
					}
				} else rule
			}

			if(fconnectionRoutingList) {
				def length = fconnectionRoutingList.size()
				if(!fconnectionRoutingList.contains("uselastreceiver")) fconnectionRoutingList << "uselastreceiver"
				((fconnectionRoutingList += connectionInstanceList) - null as Set).eachWithIndex { it, index ->
					fconnectionRoutingMap[it] = index < length
				}
			}

		 } else {
		 	fconnectionRoutingList << "uselastreceiver"
			((fconnectionRoutingList + connectionInstanceList) as Set).findAll{ fconnectionRoutingMap[it] = false }
		}

		fconnectionRoutingMap
	}

	private def remapFormParams() {
		def cType = params.connectionType
		if(!(cType in Fconnection.getImplementations(params)*.shortName)) {
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
		if (Fconnection.get(params.id)?.userMutable) {
			EnableFconnectionJob.triggerNow([connectionId:params.id])
			sleep 100 // This horrible hack allows enough time for the job to start before we try to get the status of the connection we're enabling
			def connectionInstance = Fconnection.get(params.id)
			if(connectionInstance?.shortName == 'smssync') { // FIXME should not be connection-specific code here
				smssyncService.startTimeoutCounter(connectionInstance)
			}
		}
		redirect(action:'list', params:params)
	}

	def disable() {
		withFconnection { c ->
			if(c.userMutable) {
				fconnectionService.disableFconnection(c)
			}
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

	private doAfterSaveOperations(fconnectionInstance) {
		def serviceName = "${fconnectionInstance.shortName}Service"
		def service = grailsApplication.mainContext[serviceName]
		if(service) {
			def methodName = 'afterSave'
			if(service.respondsTo(methodName) as boolean) {
				service."$methodName"(fconnectionInstance)
			}
		}
	}

	private def doSave(Class<Fconnection> clazz) {
		def connectionService = grailsApplication.mainContext["${clazz.shortName}Service"]
		def saveSuccessful
		def connectionErrors
		def fconnectionInstance
		if (connectionService && connectionService.respondsTo('handleSave')) {
			def handleSaveResponse = connectionService.handleSave(params)
			saveSuccessful = handleSaveResponse.success
			connectionErrors = handleSaveResponse.errors
			fconnectionInstance = handleSaveResponse.connectionInstance
			withFormat {
				html {
					flash.message = LogEntry.log(saveSuccessful ? handleSaveResponse.successMessage : message(code: 'connection.creation.failed', args:[handleSaveResponse.errors]))
					redirect(controller:'connection', action:"list") // FIXME - should just enable connection here and redirect to list action, surely!
				}
				json {
					render((saveSuccessful ? [ok:true, redirectUrl:createLink(action:'list')] : [ok:false, text:handleSaveResponse.errors.join(", ")]) as JSON)
				}
			}
			return
		}
		else {
			fconnectionInstance = clazz.newInstance()
			fconnectionInstance.properties = params
			fconnectionInstance.validate()
			connectionErrors = fconnectionInstance.errors.allErrors.collect { message(error:it) }
			saveSuccessful = fconnectionInstance.save(flush:true)
		}
		if(saveSuccessful) {
			doAfterSaveOperations(fconnectionInstance)
			def connectionUseSetting = appSettingsService['routing.use']
			appSettingsService['routing.use'] = connectionUseSetting?
					"$connectionUseSetting,fconnection-$fconnectionInstance.id":
					"fconnection-$fconnectionInstance.id"
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

