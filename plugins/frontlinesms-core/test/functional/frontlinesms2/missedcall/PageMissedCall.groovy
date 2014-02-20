package frontlinesms2.message

import frontlinesms2.*

class PageMissedCall extends PageMessage {
	static url = 'missedCall/inbox'

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

