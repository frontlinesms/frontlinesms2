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
		def enabledAuthentication = appSettingsService.get("auth.basic.enabled")
		def username = new String(appSettingsService.get("auth.basic.username").decodeBase64())
		def password = new String(appSettingsService.get("auth.basic.password").decodeBase64())

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
		if(appSettingsService.get("auth.basic.enabled") && appSettingsService.get("auth.basic.username") && appSettingsService.get("auth.basic.password")) {
			appSettingsService.set('auth.basic.enabled', params.enabledAuthentication)
		}
		if(params.password && params.password == params.confirmPassword) {
			appSettingsService.set('auth.basic.enabled', params.enabledAuthentication)
			appSettingsService.set('auth.basic.username', params.username.bytes.encodeBase64().toString())
			appSettingsService.set('auth.basic.password', params.password.bytes.encodeBase64().toString())
		} else if(params.password != params.confirmPassword) {
			flash.message = message(code:"basic.authentication.password.mismatch")
		}
		// render general rather than redirecting so that auth is not immediately asked for
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

