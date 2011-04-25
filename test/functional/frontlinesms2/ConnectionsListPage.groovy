package frontlinesms2

class ConnectionsListPage extends geb.Page {
	static url = 'connection/index'
	static at = {
		assert title == "Settings > Connections"
		true
	}
	static content = {
		btnNewConnection { $('#btnNewConnection') }
		lstConnections { $('#connections') }
		selectedConnection { lstConnections.find(".selected") }
	}
}