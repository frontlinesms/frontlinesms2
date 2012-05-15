package frontlinesms2.connection

import frontlinesms2.*

class ConnectionPage extends geb.Page {
	static url = 'connection/list'
	static at = {
		assert title == "Settings > Connections"
		true
	}
	static content = {
		btnNewConnection { $('#create-connection-btn').find('a') }
		lstConnections(wait:true) { $('#connections') }
		selectedConnection(required:false) { lstConnections.find(".selected") }
		btnCreateRoute(wait:true) {  $("a", text:'Create route') }
		btnTestRoute(required:false) {  $('#connections .selected a.test') }
		txtStatus { $('.connection-status').text() }
		connectionErrors { $('.errors').text()}
	}
}
