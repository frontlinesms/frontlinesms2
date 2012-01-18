package frontlinesms2.message

class PageMessageInbox extends PageMessage {
	static url = 'message/inbox'
	static at = {
		title.endsWith('Inbox')
	}
}
