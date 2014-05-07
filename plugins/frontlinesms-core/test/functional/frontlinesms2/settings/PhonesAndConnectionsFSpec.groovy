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
			btnNewConnection.text() == "connection.add"
			btnNewConnection.@href == "/connection/wizard"
	}
	
	def 'connections are listed in PHONE & CONNECTIONS panel'() {
		given:
			createTestConnections()
		when:
			go 'connection/list'
		then:
			at PageConnectionSettings
			connectionNames*.value()?.containsAll(["Miriam's Clickatell account", "MTN Dongle"])
	}

	def 'smsssync connection should display connection url'(){
		given:
			createTestConnections()
		when:
			go 'connection/list'
		then:
			at PageConnectionSettings
			connections[2].text()?.contains("http://<localhost.ip.placeholder>")
	}

	def 'Saving routing preferences persists the changes'(){
		when:
			to PageConnectionSettings
		then:
			routing.useLastReceivedConnection.@checked
		when:
			routing.useLastReceivedConnection.click()
		then:
			waitFor { at PageConnectionSettings }
			!routing.useLastReceivedConnection.@checked
	}

	def createTestConnections() {
		remote {
			SmslibFconnection.build(name:'MTN Dongle', port:'stormyPort')
			EmailFconnection.build(name:'Miriam\'s Clickatell account',
					receiveProtocol:EmailReceiveProtocol.IMAPS,
					serverName:'imap.zoho.com',
					serverPort:993,
					username:'mr.testy@zoho.com',
					password:'mister')
			SmssyncFconnection.build(name:'SmsSync to Bobs Android', secret:'trial').id
		}
	}
}

