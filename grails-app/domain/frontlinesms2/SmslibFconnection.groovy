package frontlinesms2

class SmslibFconnection extends Fconnection {
	String port
	int baud

	String type() { 'Phone/Modem' }
	String camelAddress() {
		"smslib:${port}?debugMode=true&baud=${baud}"
	}
}
