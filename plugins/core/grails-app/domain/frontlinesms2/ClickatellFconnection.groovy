package frontlinesms2

class ClickatellFconnection extends Fconnection {
	String apiId
	String username
	String password // FIXME maybe encode this rather than storing plaintext
	
	String getType() { 'Clickatell' }
	
	String getCamelProducerAddress() {
		"http://localhost:8081/mock-clickatell/http"
	}
	
	String getCamelConsumerAddress() {
		// Clickatell is used for outgoing only, so do not need to
		// consume messages
		null
	}
}
