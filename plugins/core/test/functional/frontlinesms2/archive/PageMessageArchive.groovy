package frontlinesms2.archive

import frontlinesms2.*
import frontlinesms2.archive.PageArchive

class PageMessageArchive extends frontlinesms2.message.PageMessage {
	static url = 'archive/inbox'

	String convertToPath(Object[] args) {
		def restOfPath = ""
	    if (args[0] instanceof Fmessage)
	    	restOfPath += "/show/"+(args[0] as Fmessage).id
	    else if (args[0] instanceof Number)
	    	restOfPath += "/show/"+args[0]
	    else if (args[0] instanceof Activity)
	    	restOfPath +="/show"+(args[0] as Activity).id
	    if (args.length > 1)
	    {
	    	if (args[1] instanceof Fmessage)
	    		restOfPath +="/show/"+(args[1] as Fmessage).id
	    	else if (args[1] instanceof Number)
	    		restOfPath +="/show/"+args[1]
	    }    
	    return restOfPath
	}
}