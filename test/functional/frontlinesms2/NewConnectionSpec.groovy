package frontlinesms2

class NewConnectionSpec extends ConnectionGebSpec {
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
			frmNewConnection.type = 'Email'
			frmNewConnection.name = 'test email connection'
			frmNewConnection.camelAddress = "smtp:example.com"
			btnNewConnectionSave.click()
		then:
			at ConnectionListPage
			Fconnection.count() == 1
			selectedConnection.text().startsWith('\'test email connection\' (Email)')
		cleanup:
			Fconnection.findAll().each() { it.delete(flush: true) }
	}
	
	def '"Create route" button exists and can be clicked' () {
		when:
			createTestConnection()
		    to ConnectionListPage
		then:
			def btnCreateRoute = lstCreateRouteButtons
			assert btnCreateRoute.text() == 'Create route'
		when:
			btnCreateRoute.click()
		then:
			sleep(15000)
			at ConnectionListPage
		cleanup:
			deleteTestConnection()
	}
}

class ConnectionsTypePage extends geb.Page {
	static url = 'connection/create'
	static content = {
		lstConnectionTypes { $('#connectionTypes') }
		btnNewEmailConnection { lstConnectionTypes.find('.email') }
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

