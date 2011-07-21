package frontlinesms2

import grails.util.Environment

// Please don't instantiate this class.  We would make it abstract if it didn't make testing
// difficult, and stop us calling GORM queries across all subclasses.
class Fconnection {
	def fconnectionService
	static transients = ['status', 'camelConsumerAddress', 'camelProducerAddress']
	
	String name
	
	String type() {
		if (Environment.current == Environment.TEST) {
			'unsubclassed-fconnection'
		} else throw new IllegalStateException()
	}
	
	String getCamelConsumerAddress() {
		if (Environment.current == Environment.TEST) {
			'bad:fconnection?subclassed=false'
		} else throw new IllegalStateException()
	}
	
	String getCamelProducerAddress() {
		if (Environment.current == Environment.TEST) {
			'bad:fconnection?subclassed=false'
		} else throw new IllegalStateException()
	}
	
	String getStatus() {
		fconnectionService.getRouteStatus(this)
	}
}
