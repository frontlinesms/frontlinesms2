package frontlinesms2

class SettingsController {
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
		def enabledAuthentication = appSettingsService.get("enabledAuthentication")
		def username = new String(appSettingsService.get("username").decodeBase64())
		def password = new String(appSettingsService.get("password").decodeBase64())

		[currentLanguage:i18nUtilService.getCurrentLanguage(request),
				enabledAuthentication:enabledAuthentication,
				username:username,
				password:password,
				languageList:i18nUtilService.allTranslations]
	}

	def selectLocale() {
		i18nUtilService.setLocale(request, response, params.language?:'en')
		redirect view:'general'
	}

	def basicAuth() {
		println "####basicAuth####"
		println "params:: $params"
		if(appSettingsService.get("enabledAuthentication") && appSettingsService.get("username") && appSettingsService.get("password")) {
			appSettingsService.set("enabledAuthentication", params.enabledAuthentication) 
		}
		if(params.password && params.password == params.confirmPassword) {
			appSettingsService.set("enabledAuthentication", params.enabledAuthentication) 
			appSettingsService.set("username", params."username".bytes.encodeBase64().toString()) 
			appSettingsService.set("password", params."password".bytes.encodeBase64().toString()) 
		} else if(params.password != params.confirmPassword){
			flash.message = message(code:"basic.authentication.password.mismatch")
		}
		render view:'general', model:general()
	}

	private def withFconnection(Closure c) {
		def connection = Fconnection.get(params.id)
		if(connection) {
			c connection
		} else {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])
			render(view:'show_connections', model: [fconnectionInstanceTotal: 0])
		}
	}
}

