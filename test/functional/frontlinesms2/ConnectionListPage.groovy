package frontlinesms2

class ConnectionListPage extends geb.Page {
	static url = 'connection/list'
	static at = {
		assert title == "Settings > Connections"
		true
	}
	static content = {
		btnNewConnection { $('#btnNewConnection') }
		lstConnections { $('#connections') }
		lstCreateRouteButtons { $('#createRouteButtons')}
		selectedConnection { lstConnections.find(".selected") }
	}
}
