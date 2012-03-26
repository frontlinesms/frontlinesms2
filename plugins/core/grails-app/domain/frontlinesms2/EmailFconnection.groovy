package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

class EmailFconnection extends Fconnection {
	EmailReceiveProtocol receiveProtocol
	String serverName
	Integer serverPort
	String username
	String password
	
	static passwords = ['password']
	static constraints = {
		serverPort(nullable: true)
	}
	
	List<RouteDefinition> getRouteDefinitions() {
		String serverPortParam = serverPort ? ":${serverPort}" : ""
		final String camelProducerAddress = "${receiveProtocol.toLowerCase()}://${serverName}${serverPortParam}?debugMode=true&consumer.delay=15000&username=${username}&password=${password}"
		
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from('seda:email-messages-to-send')
						.to(camelProducerAddress)
						.routeId("out-${EmailFconnection.this.id}")]
			}
		}.routeDefinitions
	}
}
