package frontlinesms2

class LogService {
	def handleRouteCreated(connection) {
		def routes = connection.routeDefinitions
		LogEntry.log("Created routes: ${routes*.id}")	
	}

	def handleRouteCreationFailed(connection) {
		LogEntry.log("Error creating routes to fconnection with name ${connection?.name?: connection?.id}")
	}
}
