package frontlinesms2.connection

import frontlinesms2.*

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

	def '"Send test message" button for particular connection appears when that connection is selected'() {
		given:
			createTestConnection()
			def testyEmail = EmailFconnection.findByName('test email connection')
		when:
			go "connection/show/${testyEmail.id}"
		then:
			$('#connections .selected .test').getAttribute('href') == "/frontlinesms2/connection/createTest/${testyEmail.id}"
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