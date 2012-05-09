package frontlinesms2.poll

import frontlinesms2.*

class PageMessagePollFootballTeamsAlice extends geb.Page {
	static getUrl() { "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}" }
	static at = {
		title.endsWith('Poll')
	}
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
		messagesSelect(required:false) { $(".message-select") }
		visibleMessageTotal(required:false) { $("#message-list .main-table tr").size() }
		newMessageNotification(required: false) { $("#new-message-notification") }
	}
}
