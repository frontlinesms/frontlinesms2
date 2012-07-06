package frontlinesms2.connection

import frontlinesms2.*

class ConnectionPage extends frontlinesms2.base.PageBase {
	static url = 'connection/list'
	static at = {
		assert title == "Settings > Connections"
		true
	}
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

class ConnectionDialog extends ConnectionPage {
	static at = {
		$("#ui-dialog-title-modalBox").text()?.toLowerCase().contains('connection')
	}
	
	static content = {
		connectionForm { $('#connectionForm')}
		doneButton { $("#submit") }
		nextPageButton { $("#nextPage") }
		confirmName { $("#confirm-name")}
		confirmType { $("#confirm-type")}
		confirmPort { $("#confirm-port")}
		confirmIntelliSmsConnectionName { $("#intellisms-confirm #confirm-name")}
		confirmIntelliSmsUserName { $("#intellisms-confirm #confirm-username")}
		confirmIntelliSmsType { $("#intellisms-confirm #confirm-type")}
	}
}
