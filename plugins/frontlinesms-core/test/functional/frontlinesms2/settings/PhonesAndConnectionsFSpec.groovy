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
			connectionNames*.text().containsAll(["'Miriam's Clickatell account'", "'MTN Dongle'"])
	}

	def 'smsssync connection should display connection url'(){
		given:
			createTestConnections()
		when:
			go 'connection/list'
		then:
			at PageConnectionSettings
			(/http:\/\/you-ip-address\/frontlinesms-core\/api\/.*\/smssync\/.*/ =~ connections[2].children().text()[2])
	}

	def createTestConnections() {
		SmslibFconnection.build(name:'MTN Dongle', port:'stormyPort')
		EmailFconnection.build(name:'Miriam\'s Clickatell account',
				receiveProtocol:EmailReceiveProtocol.IMAPS,
				serverName:'imap.zoho.com',
				serverPort:993,
				username:'mr.testy@zoho.com',
				password:'mister')
		SmssyncFconnection.build(name:'SmsSync to Bobs Android', secret:'trial')
	}
}



