package frontlinesms2

class EmailFconnection extends Fconnection {
	EmailProtocol protocol
	String serverName
	Integer serverPort
	String username
	String password

	String type() { 'Email' }
	String camelAddress() {
		String serverPortParam = serverPort ? ":${serverPort}" : ""
		"${protocol}://${serverName}${serverPortParam}?debugMode=true&consumer.delay=15000&username=${username}&password=${password}"
	}
	
	static constraints = {
		serverPort(nullable: true)
	}
}
