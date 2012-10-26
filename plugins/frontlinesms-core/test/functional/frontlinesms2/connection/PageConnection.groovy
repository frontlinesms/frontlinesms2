package frontlinesms2.connection

import frontlinesms2.*
import frontlinesms2.page.PageBase
import frontlinesms2.popup.MediumPopup
import geb.Module

class PageConnection extends PageBase {
	static url = 'connection/list'

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

class ConnectionDialog extends MediumPopup {
	static at = {
		popupTitle.contains('connection')
	}
	static content = {
		connectionType { $("#connectionType") }
		connectionForm { $('#connectionForm') }
		confirmName { $("#confirm-name") }
		confirmType { $("#confirm-type") }
		confirmPort { $("#confirm-port") }

		confirmIntelliSmsConnectionName { $("#intellisms-confirm #confirm-name") }
		confirmIntelliSmsUserName { $("#intellisms-confirm #confirm-username") }
		confirmIntelliSmsType { $("#intellisms-confirm #confirm-type") }

		confirmSmssyncName { $('#smssync-confirm #confirm-name') }
		confirmSmssyncSecret { $('#smssync-confirm #confirm-secret') }
		confirmSmssyncReceiveEnabled { $('#smssync-confirm #confirm-receiveEnabled') }
		confirmSmssyncSendEnabled { $('#smssync-confirm #confirm-sendEnabled') }

		error {$('label', class:'error')}
	}
}

