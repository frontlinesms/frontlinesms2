package frontlinesms2.message

import frontlinesms2.*

class PageMessageSent extends PageMessage {
	static url = 'message/sent'
	static at = {
		title.endsWith('Sent')
	}

	String convertToPath(Object[] args) {
		def restOfPath = ""
		if (!args)
			return ""
		if (args.length == 0)
			return ""

		if (args[0] instanceof TextMessage)
			restOfPath += "/show/"+(args[0] as TextMessage).id
		else if (args[0] instanceof Number)
			restOfPath += "/show/"+args[0]
		return restOfPath
	}
}