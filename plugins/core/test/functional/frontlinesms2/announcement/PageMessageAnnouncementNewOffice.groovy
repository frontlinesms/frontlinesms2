package frontlinesms2.announcement

import frontlinesms2.*

class PageMessageAnnouncementNewOffice extends geb.Page {
	static getUrl() { "message/activity/${Announcement.findByName('New Office').id}/show/${Fmessage.findBySrc('Max').id}" }
	static at = {
		title.endsWith('Activity')
	}
	static content = {
		messagesList { $('#messages-submenu') }
		messagesSelect(wait: true) { $(".message-select") }
		btnReplyMultiple { $('#multiple-messages a')[0] }
		btnDropdown { $("#btn_dropdown") }
		btnForward { $("#btn_forward") }
	}
}