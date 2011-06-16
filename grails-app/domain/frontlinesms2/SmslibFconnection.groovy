package frontlinesms2

class SmslibFconnection extends Fconnection {
	private def camelAddress = { "smslib:${port}?debugMode=true&baud=${baud}" }

	String port
	int baud

	String type() { 'Phone/Modem' }
	
	String getCamelConsumerAddress() {
		camelAddress()
	}
	
	String getCamelProducerAddress() {
		camelAddress()
	}
}
