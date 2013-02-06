package frontlinesms2

class ReportSmssyncTimeoutJob {
	def grailsApplication

	def execute(context) {
		def connection = Fconnection.get(context.mergedJobDataMap.get('connectionId').toLong())
		grailsApplication.mainContext.smssyncService.reportTimeout(connection)
	}
}
