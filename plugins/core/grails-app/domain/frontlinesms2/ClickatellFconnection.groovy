package frontlinesms2

class ClickatellFconnection extends Fconnection {
	String apiId
	String username
	String password // FIXME maybe encode this rather than storing plaintext
	
	static passwords = ['password']
	
	String getCamelProducerAddress() {
		"http://api.clickatell.com/http"
	}
	
	String getCamelConsumerAddress() {
		// Clickatell is used for outgoing only, so do not need to
		// consume messages
		null
	}
}
