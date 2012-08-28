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

	def 'HttpExternalCommandsFconnections should not be listed in the connections list'() {
		given:
			createTestConnections()
			def k = new Keyword(value:"TESTING")
			def c = new HttpExternalCommandFconnection(name:"test", url:"www.frontlinesms.com/sync", httpMethod:HttpExternalCommandFconnection.HttpMethod.GET)
			def e = new ExternalCommand(name:"", connection:c, keyword:k).save(failOnError:true)
		when:
			go 'connection/list'
		then:
			at PageConnectionSettings
			(connectionNames*.text().size() == 2) && (connectionNames*.text().containsAll(["'Miriam's Clickatell account'", "'MTN Dongle'"]))
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



