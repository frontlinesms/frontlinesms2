package frontlinesms2.poll

import frontlinesms2.*

class PageMessagePollFootballTeamsAlice extends geb.Page {
	static url = "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}"
	static at = {
		title.endsWith('Poll')
	}
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
		messagesSelect(required:false) { $(".message-select") }
	}
}