package frontlinesms2

class EnableFconnectionJob {
	// TODO can't we just inject the service we want here?
	def grailsApplication

	def execute(context) {
		def connection = Fconnection.get(context.mergedJobDataMap.get('connectionId').toLong())
		grailsApplication.mainContext.fconnectionService.enableFconnection(connection)
	}
}

