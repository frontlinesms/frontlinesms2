package frontlinesms2

class SmslibFconnection extends Fconnection {
	String port
	int baud

	static constraints = {
		port(nullable: false, blank: false)
	}

	String type() { 'Phone/Modem' }
	String camelAddress() {
		"smslib:${port}?debugMode=true&baud=${baud}"
	}
}
