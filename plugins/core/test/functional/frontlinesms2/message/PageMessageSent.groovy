package frontlinesms2.message

class PageMessageSent extends PageMessage {
	static url = 'message/sent'
	static at = {
		title.endsWith('Sent')
	}
}