package frontlinesms2

class EnableFconnectionJob {
	def grailsApplication

	def execute(context) {
		def connection = Fconnection.get(context.mergedJobDataMap.get('connectionId').toLong())
		grailsApplication.mainContext.fconnectionService.enableFconnection(connection)
	}
}
