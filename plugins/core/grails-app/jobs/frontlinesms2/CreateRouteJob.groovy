package frontlinesms2

class CreateRouteJob {
	def grailsApplication

	def execute(context) {
		def connection = Fconnection.get(context.mergedJobDataMap.get('connectionId').toLong())
		grailsApplication.mainContext.fconnectionService.createRoutes(connection)
	}
}
