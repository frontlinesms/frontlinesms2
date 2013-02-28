package frontlinesms2

import grails.converters.JSON

class AppInfoController {
	def appInfoService

	def index() {
		render request.JSON.collectEntries { key, value ->
			try {
				[key, appInfoService.provide(this, key, value)]
			} catch(Exception ex) {
				log.warn("Problem processing app info key=$key, value=$value", ex)
				[key, ex.message]
			}
		} as JSON
	}
}

