package frontlinesms2.message

import frontlinesms2.*

class PageMessageInbox extends PageMessage {
	static url = 'message/inbox'

	String convertToPath(Object[] args) {
		def restOfPath = ""
		if (!args)
			return ""
		if (args.length == 0)
			return ""

		if (args[0] instanceof Fmessage)
			restOfPath += "/show/"+(args[0] as Fmessage).id
		else if (args[0] instanceof Number)
			restOfPath += "/show/"+args[0]
		return restOfPath
	}
}
