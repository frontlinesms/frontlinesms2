package frontlinesms2.connection

class ConnectionShowSpec extends ConnectionGebSpec {
	def 'clicking on a connection shows us more details'() {
		given:
			createTestConnection()
			to ConnectionListPage
		when:
			lstConnections.find('h2').click()
		then:
			at ConnectionShowPage
		cleanup:
			deleteTestConnections()
	}
}

class ConnectionShowPage extends geb.Page {
	static at = {
		assert title == "Settings > Connections > test email connection"
		true
	}
}