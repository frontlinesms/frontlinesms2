package frontlinesms2.folder

import frontlinesms2.*

class PageMessageFolderWork extends geb.Page {
	static url = "message/folder/${Folder.findByName('Work').id}/show/${Fmessage.findBySrc('Max').id}"
	static at = {
		title.endsWith('Folder')
	}
	static content = {
		messagesList { $('#messages-submenu') }
		messagesSelect(wait: true) { $(".message-select") }
		btnReplyMultiple { $('#multiple-messages a')[0] }
		btnDropdown { $("#btn_dropdown") }
		btnForward { $("#btn_forward") }
	}
}