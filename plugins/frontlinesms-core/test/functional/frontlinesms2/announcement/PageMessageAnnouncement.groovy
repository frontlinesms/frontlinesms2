package frontlinesms2.announcement

import frontlinesms2.*

class PageMessageAnnouncement extends frontlinesms2.page.PageMessageActivity {
	static url = 'message/activity'
	static content = {
		moreActions { $('div.header-buttons select#more-actions') }
		messageSender { $('#main-list tr .message-sender-cell') }
		rowContents { $('#main-list tr:nth-child(2) td')*.text() }
		selectedMenuItem {$('ul.submenu li.selected')}
		messagesList { $('#main-list tr') }
		messagesSelect(wait: true) { $(".message-select") }
	}
}
