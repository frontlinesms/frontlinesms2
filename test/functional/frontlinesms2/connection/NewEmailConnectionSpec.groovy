package frontlinesms2.connection

import frontlinesms2.*

class NewEmailConnectionSpec extends ConnectionGebSpec {
	def 'email connection type is available from new connections page' () {
		when:
			to ConnectionsTypePage
		then:
			btnNewEmailConnection.text() == "Email"
	}

	def 'can save new email connection' () {
		when:
			to ConnectionsTypePage
			btnNewEmailConnection.click()
		then:
			at NewEmailConnectionPage
		when:
			frmNewConnection.name = 'test email connection'
			frmNewConnection.protocol = 'imap'
			frmNewConnection.serverName = 'mail.example.com'
			frmNewConnection.serverPort = '1234'
			frmNewConnection.username = 'greg'
			frmNewConnection.password = 'pastie'
			btnNewConnectionSave.click()
		then:
			at ConnectionListPage
			Fconnection.count() == 1
			selectedConnection.find('h2').text() == 'test email connection'
			selectedConnection.find('h3').text() == 'Email'
		cleanup:
			deleteTestConnections()
	}
	
	def '"Create route" button exists and can be clicked only once' () {
		when:
			createTestConnection()
			to ConnectionListPage
		then:
			btnCreateRoute.text() == 'Create route'
		when:
			btnCreateRoute.click()
		then:
			!btnCreateRoute.present
		cleanup:
			deleteTestConnections()
	}
}

class NewEmailConnectionPage extends geb.Page {
	static at = {
		assert title.endsWith('Configure new email connection')
		true
	}

	static content = {
		frmNewConnection { $('form.newConnection') }
		btnNewConnectionSave {frmNewConnection.find('.create')}
	}
}
