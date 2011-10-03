package frontlinesms2.settings

import frontlinesms2.*


class PhonesAndConnectionsSettingsSpec extends grails.plugin.geb.GebSpec {
	
	def 'add new connection option is available in connection settings panel'() {
		given:
			createTestConnections()
		when:
			to ConnectionPage
		then:
			btnNewConnection.text() == "Add new connection"
			assert btnNewConnection.getAttribute("href") == "/frontlinesms2/connection/create_new"
	}
	

	def 'connections are listed in PHONE & CONNECTIONS panel'() {
		given:
			createTestConnections()
		when:
			to ConnectionPage
		then:
			connectionNames != null
			connectionNames.find('a')*.text() == ["'MTN Dongle'", "'Miriam's Clickatell account'"]
	}

	def createTestConnections() {
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
		}
	}
}

class ConnectionPage extends geb.Page {
	static url = 'settings/connections'
	static content = {
		btnNewConnection { $('#btnNewConnection a') }
		connectionNames { $('.con-name') }
	}
}


