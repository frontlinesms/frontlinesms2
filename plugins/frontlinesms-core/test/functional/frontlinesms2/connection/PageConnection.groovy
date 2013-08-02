package frontlinesms2.connection

import frontlinesms2.*
import frontlinesms2.page.PageBase
import frontlinesms2.popup.MediumPopup
import geb.Module

class PageConnection extends PageBase {
	String convertToPath(Object[] args) {
		if(!args) 'connection/list'
		else 'connection/list/' + args[0]
	}

	static content = {
		connectionList { module ConnectionList }
		noContent { $("div#body-content p.no-content") }
		btnNewConnection(wait:true) { $(".btn", text: 'Add new connection') }
		connectionFailedFlashMessageEditButton { // technically not tied to this page - could be defined elsewhere if useful
			notifications.systemNotification.find('a', text:'edit')
		}
	}
}

class ConnectionList extends Module {
	static base = { $('div#body-content .connection-list') }
	static content = {
		listSize { $("tbody tr.connection").size() }
		connection { i=0 -> $('tbody tr.connection', i) }
		hoverOn { i -> connection(i).find('div.controls').jquery.css("visibility", "visible") }
		connectionName { i-> connection(i).find("td.connection-name").text() }
		btnEnableRoute(wait:true) { i -> hoverOn(i) ; connection(i).find(".btn", text:'Enable') }
		btnRetryConnection(wait:true) { i ->  hoverOn(i) ; connection(i).find(".btn", text:'Retry') }
		btnDisableRoute(wait:true) { i -> hoverOn(i) ; connection(i).find(".btn", text:'Disable') }
		btnDelete(required:false) { i -> hoverOn(i) ; connection(i).find('.btn', text:'Delete') }
		btnTestRoute(required:false) { i -> hoverOn(i) ; connection(i).find('.btn', text:'Send test message') }
		status { i -> connection(i).find('td.connection-status') }
	}
}

