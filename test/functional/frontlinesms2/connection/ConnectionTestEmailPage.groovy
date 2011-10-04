package frontlinesms2.connection

import frontlinesms2.*

class ConnectionShowPage extends geb.Page {
	static at = {
		assert title == "Settings > Connections > test email connection"
		true
	}
	
	static content = {
		lstConnections { $('#connections') }
		selectedConnection(required:false) { lstConnections.find(".selected") }
	}
}

