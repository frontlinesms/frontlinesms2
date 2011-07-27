package frontlinesms2

class SmslibFconnection extends Fconnection {
	private def camelAddress = { "smslib:${port}?debugMode=true&baud=${baud}" }

	String port
	int baud

	static constraints = {
		port(nullable: false, blank: false)
	}

	String type() { 'Phone/Modem' }
	
	String getCamelConsumerAddress() {
		camelAddress()
	}
	
	String getCamelProducerAddress() {
		camelAddress()
	}
}
