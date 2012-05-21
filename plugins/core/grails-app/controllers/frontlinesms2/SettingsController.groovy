package frontlinesms2

import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.support.RequestContextUtils
import org.springframework.util.StringUtils

class SettingsController {
	def i18nUtilService
	def index = {
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

	def general = {
		[languageList:i18nUtilService.allTranslations]
	}

	def selectLocale = {
		Locale locale = StringUtils.parseLocaleString(params.language)
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request)
		localeResolver.setLocale(request, response, locale)
		redirect view:'general'
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

