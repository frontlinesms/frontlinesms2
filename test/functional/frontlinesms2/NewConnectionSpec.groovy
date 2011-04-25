package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class NewConnectionSpec extends grails.plugin.geb.GebSpec {
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
			at ConnectionsListPage
			Fconnection.count() == 1
			selectedConnection.text() == '\'test email connection\' (Email)'
		cleanup:
			Fconnection.findAll().each() { it.delete(flush: true) }
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

