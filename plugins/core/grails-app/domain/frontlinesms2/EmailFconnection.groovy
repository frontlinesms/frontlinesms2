package frontlinesms2

class EmailFconnection extends Fconnection {
	EmailReceiveProtocol receiveProtocol
	String serverName
	Integer serverPort
	String username
	String password

	String type() { 'Email' }

	String getCamelConsumerAddress() {
		String serverPortParam = serverPort ? ":${serverPort}" : ""
		"${receiveProtocol}://${serverName}${serverPortParam}?debugMode=true&consumer.delay=15000&username=${username}&password=${password}"
	}

	String getCamelProducerAddress() {
		null
	}
	
	static constraints = {
		serverPort(nullable: true)
	}
}
