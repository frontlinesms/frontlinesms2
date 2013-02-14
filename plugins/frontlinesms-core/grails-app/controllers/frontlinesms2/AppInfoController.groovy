package frontlinesms2

import grails.converters.JSON

class AppInfoController {
	def appInfoService

	def index() {
		render getInterest().collectEntries { key ->
			[(key):appInfoService.provide(key, this)]
		} as JSON
	}

	private def getInterest() {
		def interest = params.'interest[]'
		if(!interest) {
			return []
		}
		if(interest instanceof String) {
			return [interest]
		}
		return interest
	}
}

