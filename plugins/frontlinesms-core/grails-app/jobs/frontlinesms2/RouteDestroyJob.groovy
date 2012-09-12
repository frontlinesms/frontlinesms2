package frontlinesms2

class RouteDestroyJob {
	def grailsApplication

	def execute(context) {
		grailsApplication.mainContext.fconnectionService.destroyRoutes(context.mergedJobDataMap.get('routeId'))
	}
}

