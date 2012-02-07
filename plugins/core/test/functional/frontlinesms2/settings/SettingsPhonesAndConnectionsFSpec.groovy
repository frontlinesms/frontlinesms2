package frontlinesms2.settings

import frontlinesms2.*

class SettingsPhonesAndConnectionsFSpec extends SettingsBaseSpec {
	
	def 'add new connection option is available in connection settings panel'() {
		given:
			createTestConnections()
		when:
			go 'settings/connections'
		then:
			at PageSettingsConnection
			btnNewConnection.text() == "Add new connection"
			btnNewConnection.@href == "/connection/create_new"
	}
	
	def 'connections are listed in PHONE & CONNECTIONS panel'() {
		given:
			createTestConnections()
		when:
			go 'settings/connections'
		then:
			at PageSettingsConnection
			connectionNames*.text() == ["'MTN Dongle'", "'Miriam's Clickatell account'"]
	}
}



