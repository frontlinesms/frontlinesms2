package frontlinesms2.settings

import frontlinesms2.*

class SettingsPhonesAndConnectionsFSpec extends SettingsBaseSpec {
	
	def 'add new connection option is available in connection settings panel'() {
		given:
			createTestConnections()
		when:
			to ConnectionPage
		then:
			btnNewConnection.text() == "Add new connection"
			assert btnNewConnection.getAttribute("href") == "/frontlinesms2/connection/create_new"
	}
	

	def 'connections are listed in PHONE & CONNECTIONS panel'() {
		given:
			createTestConnections()
		when:
			to ConnectionPage
		then:
			connectionNames != null
			connectionNames.find('a')*.text() == ["'MTN Dongle'", "'Miriam's Clickatell account'"]
	}
}



