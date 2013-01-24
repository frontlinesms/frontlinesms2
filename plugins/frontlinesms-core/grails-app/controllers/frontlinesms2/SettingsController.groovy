package frontlinesms2


class SettingsController extends ControllerUtils {
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

		appSettings['routing.uselastreceiver'] = appSettingsService.get("routing.uselastreceiver")
		appSettings['routing.otherwise'] = appSettingsService.get("routing.otherwise")
		appSettings['routing.rules'] = appSettingsService.get("routing.rules")

		def routingRulesMap = getRoutingRules(appSettings['routing.rules'])

		[currentLanguage:i18nUtilService.getCurrentLanguage(request),
				authEnabled:authEnabled,
				username:username,
				password:password,
				languageList:i18nUtilService.allTranslations,
				routingRulesMap:routingRulesMap,
				appSettings:appSettings]
	}

	def selectLocale() {
		i18nUtilService.setLocale(request, response, params.language?:'en')
		redirect view:'general'
	}

	def basicAuth() {
		println "params:: $params"
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
		appSettingsService.set('routing.uselastreceiver', params.uselastreceiver? 'true': 'false')
		appSettingsService.set('routing.rules', "${processConnectionRules(params)}")
		appSettingsService.set('routing.otherwise', params.otherwise)
		redirect action:'general'
	}

	private def processConnectionRules(params) {
		def routingRules
		routingRules = params.findAll {it.key == "uselastreceiver" || it.key ==~ /fconnection-\d/}
		routingRules.getAllKeys().join(",")
	}

	private getRoutingRules(routingRules) {
		def routingRuleList = []
		def routingRulesMap = [:]
		def connectionInstanceList = Fconnection.findAll()

		if(routingRules) {
			routingRuleList = routingRules?.tokenize(",")?.flatten()
			println "Routing Rules before refinement:::: $routingRules"

			// Replacing fconnection rules with fconnection instances
			routingRuleList = routingRuleList.collect { rule ->
				if(rule.contains("fconnection-"))  connectionInstanceList.find {
						println "Comparing rule:: $rule with id:: $it ::  ${it.id == ((rule - 'fconnection-') as Integer)}"
						it.id == ((rule - 'fconnection-') as Integer)
					}
				else rule
			}

			if(routingRuleList) {
				def length = routingRuleList.size()
				if(!routingRuleList.contains("uselastreceiver")) routingRuleList << "uselastreceiver"
				((routingRuleList += connectionInstanceList) - null as Set).eachWithIndex {it, index -> 
					if(index < length) routingRulesMap[it] = true
					else routingRulesMap[it] = false
				}
			}

		 } else {
		 	routingRuleList << "uselastreceiver"
			((routingRuleList + connectionInstanceList) as Set).findAll{ routingRulesMap[it] = false }
		}
	}

	private def withFconnection = withDomainObject Fconnection, { params.id }, { render(view:'show_connections', model: [fconnectionInstanceTotal: 0]) }
}

