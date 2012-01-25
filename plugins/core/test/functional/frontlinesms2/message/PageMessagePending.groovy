package frontlinesms2.message

class PageMessagePending extends PageMessage {
	static url = 'message/pending'
	static at = {
		title.endsWith('Pending')
	}
}