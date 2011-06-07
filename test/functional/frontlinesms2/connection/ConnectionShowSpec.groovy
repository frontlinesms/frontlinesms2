package frontlinesms2.connection

class ConnectionShowSpec extends ConnectionGebSpec {
	def 'clicking on a connection shows us more details'() {
		given:
			createTestSMSConnection()
			to ConnectionListPage
		when:
			lstConnections.find('h2').click()
		then:
			$('title').text() == "Settings > Connections > MTN Dongle"
		cleanup:
			deleteTestConnections()
	}
}

class ConnectionShowPage extends geb.Page {
	static at = {
		assert title == "Settings > Connections > test email connection"
		true
	}
	
	static content = {
		lstConnections { $('#connections') }
		selectedConnection(required:false) { lstConnections.find(".selected") }
	}
}