package frontlinesms2

import grails.converters.JSON
class AutoforwardController extends ActivityController {
	def autoforwardService

	def save() {
		withAutoforward { autoforward ->
			doSave('autoreply', autoforwardService, autoforward)
		}
	}

	private def withAutoforward = withDomainObject Autoforward
}

