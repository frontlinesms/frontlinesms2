package frontlinesms2

import grails.converters.JSON

class AppInfoController {
	//FIXME index method should only accept GET requests but POST is set to prevent rendering of blank pages
	static allowedMethods = [index: "POST"] 
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

