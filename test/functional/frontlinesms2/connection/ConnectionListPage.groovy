package frontlinesms2.connection

import frontlinesms2.*

class ConnectionListPage extends geb.Page {
	static url = 'connection/list'
	static at = {
		assert title == "Settings > Connections"
		true
	}
	static content = {
		btnNewConnection { $('#btnNewConnection') }
		lstConnections { $('#connections') }
		selectedConnection { lstConnections.find(".selected") }
		btnCreateRoute(required:false) {  lstConnections.find('.buttons a').first() }
		btnCreateFirstRoute { $('.buttons') }
	}
}
