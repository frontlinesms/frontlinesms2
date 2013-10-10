package frontlinesms2.message

import frontlinesms2.*

class PageMessageInbox extends PageMessage {
	static url = 'message/inbox'

	String convertToPath(Object[] args) {
		if (!args) {
			return ''
		}

		if (args[0] instanceof Number) {
			return "/show/${args[0]}"
		}

		return ''
	}
}

