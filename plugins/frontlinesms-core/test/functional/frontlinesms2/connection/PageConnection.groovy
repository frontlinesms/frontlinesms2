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
		btnNewConnection(wait:true) { $(".btn", text: 'connection.add') }
		connectionFailedFlashMessageEditButton { // technically not tied to this page - could be defined elsewhere if useful
			def n = notifications.systemNotification
			if(!(n.text() ==~ /(?s).*#" onclick="mediumPopup\.editConnection\(\d+\).*/)) {
				return new geb.navigator.EmptyNavigator(browser)
			}
			return [
				click:{ ->
					js.exec((delegate.text() =~ /onclick="(.*?)[",]/)[0][1])
				},
				displayed:true
			]
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
		btnEnableRoute(wait:true) { i -> hoverOn(i) ; connection(i).find(".btn", text:'connection.route.enable') }
		btnRetryConnection(wait:true) { i ->  hoverOn(i) ; connection(i).find(".btn", text:'connection.route.retryconnection') }
		btnDisableRoute(wait:true) { i -> hoverOn(i) ; connection(i).find(".btn", text:'connection.route.disable') }
		btnDelete(required:false) { i -> hoverOn(i) ; connection(i).find('.btn', text:'connection.delete') }
		btnTestRoute(required:false) { i -> hoverOn(i) ; connection(i).find('.btn', text:'connection.send.test.message') }
		status { i -> connection(i).find('td.connection-status') }
		smssyncUrl { i -> connection(i).find('p.api-url') }
		smssyncLastconnected { i -> connection(i).find('em.smssync-lastconnected') }
		frontlineSyncConfigExpander { i -> connection(i).find('a.sync-config-status-toggler') }
		frontlineSyncConfigHolder(required:false) { i -> connection(i).find('div.sync-config-container') }
		frontlineSyncSendEnabled { i -> connection(i).find('#sendEnabled') }
		frontlineSyncReceiveEnabled { i -> connection(i).find('#receiveEnabled') }
		frontlineSyncMissedCallEnabled { i -> connection(i).find('#missedCallEnabled') }
		frontlineSyncCheckFrequencyValue { i -> connection(i).find('input[name=checkIntervalIndex]').jquery.val() }
		setFrontlineSyncCheckFrequencyValue { val -> js.exec "\$('input[name=checkIntervalIndex]').simpleSlider('setValue', ${val});" }
		frontlineSyncSaveConfig { i -> connection(i).find('#sync-config-button') }
	}
}

