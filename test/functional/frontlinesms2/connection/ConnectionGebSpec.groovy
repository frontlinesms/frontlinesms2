package frontlinesms2.connection

import frontlinesms2.*

class ConnectionGebSpec extends grails.plugin.geb.GebSpec{

	static createTestConnection() {
		[new Fconnection(name: 'test email connection', type: 'Email', camelAddress: 'imaps://imap.zoho.com:993?username=mr.testy@zoho.com&password=mister&debugMode=true&consumer.delay=15000')].each() { it.save(failOnError:true) }
	}

	static deleteTestConnection() {
		Fconnection.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}
