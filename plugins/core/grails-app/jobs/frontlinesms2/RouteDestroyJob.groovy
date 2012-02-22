package frontlinesms2

class RouteDestroyJob {
	static triggers = {
		custom name:'neverTrigger', triggerClass:NeverTrigger
	}
	
	def fconnectionService

	def execute(context) {
		fconnectionService.destroyRoutes(context.mergedJobDataMap.get('routeId'))
	}
}

