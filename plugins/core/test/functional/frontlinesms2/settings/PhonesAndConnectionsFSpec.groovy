package frontlinesms2.settings

import frontlinesms2.*

class PhonesAndConnectionsFSpec extends grails.plugin.geb.GebSpec {
	
	def 'add new connection option is available in connection settings panel'() {
		given:
			createTestConnections()
		when:
			go 'connection/list'
		then:
			at PageConnectionSettings
			btnNewConnection.text() == "Add new connection"
			btnNewConnection.@href == "/connection/wizard"
	}
	
	def 'connections are listed in PHONE & CONNECTIONS panel'() {
		given:
			createTestConnections()
		when:
			go 'connection/list'
		then:
			at PageConnectionSettings
			connectionNames*.text() == ["'Miriam's Clickatell account'", "'MTN Dongle'"]
	}

	def createTestConnections() {
		SmslibFconnection.build(name:'MTN Dongle', port:'stormyPort')
		EmailFconnection.build(name:'Miriam\'s Clickatell account',
				receiveProtocol:EmailReceiveProtocol.IMAPS,
				serverName:'imap.zoho.com',
				serverPort:993,
				username:'mr.testy@zoho.com',
				password:'mister')
	}
}



