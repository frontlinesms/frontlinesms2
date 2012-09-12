package frontlinesms2.connection

import frontlinesms2.*

class PageConnection extends frontlinesms2.page.PageBase {
	static url = 'connection/list'

	static content = {
		connectionList { module ConnectionList }
		btnNewConnection(wait:true) { $(".btn", text: 'Add new connection') }
	}
}

class ConnectionList extends geb.Module {
	static base = { $('div#body-content.connections') }
	static content = {
		connection(required:false) { $("li.connection") }
		selectedConnection(required:false) { $("li.connection.selected") }
		btnCreateRoute(wait:true) {  $(".btn", text:'Create route') }
		btnDelete(required:false) { $('.btn', text:'Delete Connection') }
		btnTestRoute(required:false) {  $('.btn', text:'Send test message') }
		status { $('p.connection-status').text() }
	}
}

