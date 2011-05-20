package frontlinesms2.settings

import frontlinesms2.*
import frontlinesms2.connection.ConnectionListPage

class PhonesAndConnectionsSettingsSpec extends grails.plugin.geb.GebSpec {
	
	def 'add new connection option is available in connection settings panel'() {
		when:
			to ConnectionListPage
		then:
			btnNewConnection.text() == "Add new connection"
			assert btnNewConnection.children().getAttribute("href") == "/frontlinesms2/connection/create"
	}
	

	def 'connections are listed in "phone & connections" panel'() {
		given:
			createTestConnections()
		when:
			to ConnectionListPage
		then:
			lstConnections != null
			lstConnections.find('h2')*.text() == ['MTN Dongle', 'David\'s Clickatell account', 'Miriam\'s Clickatell account']
		cleanup:	
			deleteTestConnections()
	}

	def createTestConnections() {
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'David\'s Clickatell account', protocol:EmailProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', protocol:EmailProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
		}
	}

	def deleteTestConnections() {
		SmslibFconnection.findAll().each() { it.delete(flush: true) }
		EmailFconnection.findAll().each() { it.delete(flush: true) }
		Fconnection.findAll().each() { it.delete(flush: true) }
	}
}


