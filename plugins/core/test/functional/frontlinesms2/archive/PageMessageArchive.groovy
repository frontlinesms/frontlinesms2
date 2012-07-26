package frontlinesms2.archive

import frontlinesms2.*
import frontlinesms2.archive.PageArchive

class PageMessageArchive extends frontlinesms2.message.PageMessage {
	static url = 'archive'

	String convertToPath(Object[] args) {
		def restOfPath = ""
		if (args[0] instanceof Fmessage)
			restOfPath += "/inbox/show/"+(args[0] as Fmessage).id
	    else if (args[0] instanceof Number)
			restOfPath += "/inbox/show/"+args[0]
		else
			restOfPath += "/activity/"+Activity.findByName(args[0])?.id+"?messageSection=activity&viewingMessages=true"
		return restOfPath
	}

	static content = {
		archiveAll {$('a', text:'Archive all')}
	}
}