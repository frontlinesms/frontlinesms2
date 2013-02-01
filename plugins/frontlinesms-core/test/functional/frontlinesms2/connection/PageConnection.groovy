package frontlinesms2.connection

import frontlinesms2.*
import frontlinesms2.page.PageBase
import frontlinesms2.popup.MediumPopup
import geb.Module

class PageConnection extends PageBase {
	String convertToPath(Object[] args) {
		if(!args) 'connection/list'
		else if(args[0] instanceof Number) 'connection/show/' + args[0]
		else 'connection/show/' + args[0].id
	}

	static content = {
		connectionList { module ConnectionList }
		btnNewConnection(wait:true) { $(".btn", text: 'Add new connection') }
		connectionFailedFlashMessageEditButton { // technically not tied to this page - could be defined elsewhere if useful
			notifications.systemNotification.find('a', text:'edit')
		}
	}
}

class ConnectionList extends Module {
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

