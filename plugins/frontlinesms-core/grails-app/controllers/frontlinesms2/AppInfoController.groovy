package frontlinesms2

import grails.converters.JSON

class AppInfoController {
	def appInfoService

	def index() {
		render request.JSON.collectEntries { key ->
			[(key):appInfoService.provide(key, this)]
		} as JSON
	}

	private def listDetected() {
		detectedDevices:deviceDetectionService.detected
	}
}

