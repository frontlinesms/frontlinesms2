package frontlinesms2

class SmslibFconnection extends Fconnection {
	private def camelAddress = { "smslib:$port?debugMode=true&baud=$baud&pin=$pin&allMessages=$allMessages" }

	String port
	int baud
	String pin
	boolean allMessages = true

	static constraints = {
		port(nullable: false, blank: false)
		pin(nullable: true)
	}

	String type() { 'Phone/Modem' }
	
	String getCamelConsumerAddress() {
		camelAddress()
	}
	
	String getCamelProducerAddress() {
		camelAddress()
	}
}
