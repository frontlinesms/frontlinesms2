package frontlinesms2.connection

import frontlinesms2.*

class ConnectionPage extends geb.Page {
	static url = 'settings/connections'
	static at = {
		assert title == "Settings > Connections"
		true
	}
	static content = {
		btnNewConnection { $('#create-connection-btn').find('a') }
		lstConnections { $('#connections') }
		selectedConnection(required:false) { lstConnections.find(".selected") }
		btnCreateRoute(required:false) {  lstConnections.find('.buttons a').first() }
		btnCreateFirstRoute { $('.buttons') }
	}
}
