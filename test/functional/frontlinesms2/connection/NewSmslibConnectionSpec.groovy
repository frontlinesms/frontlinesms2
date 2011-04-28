package frontlinesms2.connection

import frontlinesms2.Fconnection

class NewSmslibConnectionSpec extends ConnectionGebSpec {
	def 'smslib connection type is available from new connections page' () {
		when:
			to ConnectionsTypePage
		then:
			btnNewSmslibConnection.text() == "Phone/Modem"
	}

	def 'can save new smslib connection' () {
		when:
			to ConnectionsTypePage
			btnNewSmslibConnection.click()
		then:
			at NewSmslibConnectionPage
		when:
			frmNewConnection.type = 'Phone/Modem'
			frmNewConnection.name = 'test smslib connection'
			frmNewConnection.camelAddress = "smslib:COM1?baud=9600&debugMode=true"
			btnNewConnectionSave.click()
		then:
			at ConnectionListPage
			Fconnection.count() == 1
			selectedConnection.find('h2').text() == 'test smslib connection'
			selectedConnection.find('h3').text() == 'Phone/Modem'
		cleanup:
			deleteTestConnections()
	}
}
