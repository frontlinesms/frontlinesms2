package frontlinesms2

class CreateRouteJob {
	static triggers = {
		custom name:'createTrigger', triggerClass:NeverTrigger
	}
	
	def fconnectionService

	def execute(context) {
		def connection = Fconnection.get(context.mergedJobDataMap.get('connectionId').toLong())
		fconnectionService.createRoutes(connection)
	}
}
