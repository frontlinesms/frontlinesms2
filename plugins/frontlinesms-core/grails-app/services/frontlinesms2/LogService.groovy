package frontlinesms2

import grails.events.Listener

class LogService {
	@Listener(topic="routeCreated", namespace="fconnection")
	def handleRouteCreated(connection) {
		def routes = connection.routeDefinitions
		LogEntry.log("Created routes: ${routes*.id}")	
	}

	@Listener(topic="routeCreationFailed", namespace="fconnection")
	def handleRouteCreationFailed(connection) {
		LogEntry.log("Error creating routes to fconnection with name ${connection?.name?: connection?.id}")
	}
}
