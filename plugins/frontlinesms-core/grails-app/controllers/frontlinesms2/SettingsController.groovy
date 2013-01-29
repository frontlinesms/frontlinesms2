package frontlinesms2

class SettingsController extends ControllerUtils {
	static final String RULE_PREFIX = "fconnection-"
	
	def i18nUtilService
	def appSettingsService

	def index() {
		redirect(action:'general')
	}
	
	def logs() {
		def logEntryList
		if(params.timePeriod && params.timePeriod != 'forever') {
			def timePeriod = new Date() - params.timePeriod.toInteger()
			logEntryList = LogEntry.findAllByDateGreaterThanEquals(timePeriod)
		} else {
			logEntryList = LogEntry.findAll()
		}
		[logEntryList:logEntryList,
				logEntryTotal:logEntryList.size()]
	}
	
	def logsWizard() {
		return [action: 'logs']
	}

	def general() {
		def authEnabled = appSettingsService.get("auth.basic.enabled")
		def username = new String(appSettingsService.get("auth.basic.username").decodeBase64())
		def password = new String(appSettingsService.get("auth.basic.password").decodeBase64())
		def appSettings = [:] 
		
		//TODO Filter out only connections with send enabled

		appSettings['routing.otherwise'] = appSettingsService.get("routing.otherwise")
		appSettings['routing.use'] = appSettingsService.get("routing.use")

		def fconnectionRoutingMap = getRoutingRules(appSettings['routing.use'])

		[currentLanguage:i18nUtilService.getCurrentLanguage(request),
				authEnabled:authEnabled,
				username:username,
				password:password,
				languageList:i18nUtilService.allTranslations,
				fconnectionRoutingMap:fconnectionRoutingMap,
				appSettings:appSettings]
	}

	def selectLocale() {
		i18nUtilService.setLocale(request, response, params.language?:'en')
		redirect view:'general'
	}

	def basicAuth() {
		if(appSettingsService.get("auth.basic.enabled") && appSettingsService.get("auth.basic.username") && appSettingsService.get("auth.basic.password")) {
			appSettingsService.set('auth.basic.enabled', params.enabled)
		}
		if(params.password && params.password == params.confirmPassword) {
			appSettingsService.set('auth.basic.enabled', params.enabled)
			appSettingsService.set('auth.basic.username', params.username.bytes.encodeBase64().toString())
			appSettingsService.set('auth.basic.password', params.password.bytes.encodeBase64().toString())
		} else if(params.password != params.confirmPassword) {
			flash.message = message(code:"auth.basic.password.mismatch")
		}
		// render general rather than redirecting so that auth is not immediately asked for
		render view:'general', model:general()
	}

	def changeRoutingPreferences() {
		println "params:: $params"

		appSettingsService.set('routing.use', params.routingUseOrder)
		appSettingsService.set('routing.otherwise', params.otherwise)
		redirect action:'general'
	}

	private getRoutingRules(routingRules) {
		def fconnectionRoutingList = []
		def fconnectionRoutingMap = [:]
		def connectionInstanceList = Fconnection.findAllBySendEnabled(true)

		if(routingRules) {
			fconnectionRoutingList = routingRules.split(/\s*,\s*/)
			println "Routing Rules before refinement:::: $routingRules"

			// Replacing fconnection rules with fconnection instances
			fconnectionRoutingList = fconnectionRoutingList.collect { rule ->
				if(rule.startsWith(RULE_PREFIX)) {
					connectionInstanceList.find {
						println "Comparing rule:: $rule with id:: $it ::  ${it.id == ((rule - RULE_PREFIX) as Integer)}"
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

	private def withFconnection = withDomainObject Fconnection, { params.id }, { render(view:'show_connections', model: [fconnectionInstanceTotal: 0]) }
}

