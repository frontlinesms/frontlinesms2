package frontlinesms2

class TestWebconnectionJob {
    def webconnectionService

	def execute(context) {
		def webconnection = Webconnection.get(context.mergedJobDataMap.get('webconnectionId').toLong())
		webconnectionService.testRoute(webconnection)
	}
}
