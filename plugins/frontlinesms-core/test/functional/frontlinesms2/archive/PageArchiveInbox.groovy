package frontlinesms2.archive
import frontlinesms2.*

class PageArchiveInbox extends PageArchive {
	static def url = "archive"

	String convertToPath(Object[] args) {
		if (!args)
			return ""
		if (args.length == 0)
			return ""
		def restOfPath = ""
		if (args[0] instanceof Fmessage)
			restOfPath += "/inbox/show/"+(args[0] as Fmessage).id
	    else if (args[0] instanceof Number)
			restOfPath += "/inbox/show/"+args[0]
		else
			restOfPath += "/activity/"+Activity.findByName(args[0])?.id+"?messageSection=activity&viewingMessages=true"
		return restOfPath
	}
	
	static at = {
		title.endsWith('inbox')
	}
}
