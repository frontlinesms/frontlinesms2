package frontlinesms2

class PhonesAndConnectionsSettingsSpec extends grails.plugin.geb.GebSpec {
	def '"phone settings & connections" menu item is available settings menu'() {
		when:
			to SettingsPage
		then:
			phonesMenuItem.text() == "Phones & connections"
			phonesMenuItem.children('a').getAttribute('href') == "/frontlinesms2/settings/connections"
	}
	
	def 'add new connection option is available in phone settings panel'() {
		when:
			to ConnectionsListPage	
		then:
			btnNewConnection.text() == "Add new connection"
		when:
			btnNewConnection.click()
		then:
			at NewConnectionPage
	}
	
	def 'connections are listed in "phone & connections" panel'() {
		given:
			mockConnections()
		when:
			at ConnectionsListPage
		then:
			lstConnections != null
			lstConnections.children().collect() {
				it.text()
			} == ["'MTN Dongle' (Phone/Modem)", "'David's Clickatell account' (Clickatell SMS Gateway)", "'Miriam's Clickatell account' (Clickatell SMS Gateway)"]
	}

	def mockConnections() {
		def mocks = [new Fconnection(name:'MTN Dongle', type:'Phone/Modem'),
				new Fconnection(name:'David\'s Clickatell account', type:'Clickatell SMS Gateway'),
				new Fconnection(name:'Miriam\'s Clickatell account', type:'Clickatell SMS Gateway')]
		mockDomain(Connection, mocks)
	}
}

class ConnectionsListPage extends geb.Page {
	static url = 'settings/connections'
	static content = {
		btnNewConnection { $('#btnNewConnection') }
		lstConnections { $('#connections') }
	}
}

class NewConnectionPage extends geb.Page {
	static at = {
		title.endsWith('Add new connection')
	}
}

