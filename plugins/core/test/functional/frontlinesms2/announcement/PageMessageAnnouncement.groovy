package frontlinesms2.announcement

import frontlinesms2.*

class PageMessageAnnouncement extends frontlinesms2.page.PageMessageActivity {
	static url = 'message/activity'
	static content = {
		moreActions { $('div.header-buttons select#more-actions') }
		messageSender { $('#main-list tr .message-sender-cell') }
		rowContents { $('#main-list tr:nth-child(2) td')*.text() }
		selectedMenuItem {$('ul.submenu li.selected')}
		reply {$("#btn_reply")}
		messagesList { $('#main-list tr') }
		starredMessages {$('a', text:'Starred')}
		allMessages {$('a', text:'All')}
		done {$("#done")}
		messagesSelect(wait: true) { $(".message-select") }
		btnReplyMultiple { $('#multiple-messages a')[0] }
		btnDropdown { $("#btn_dropdown") }
		messageCount {$("#checked-message-count")}
		messageDetailsSender {$("#message-detail-sender")}
	}
}
