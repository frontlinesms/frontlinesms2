package frontlinesms2.folder

import frontlinesms2.*
import frontlinesms2.message.*

class PageMessageFolder extends PageMessage {
	static url = 'message/folder'

	String convertToPath(Object[] args) {
		println("Building url...")
		def restOfPath = ""
		if (args.length == 0)
			return ""

	    if (args[0] instanceof Folder)
	    	restOfPath += "/"+(args[0] as Folder).id
	    else if (args[0] instanceof Number)
	    	restOfPath += "/"+args[0]	

	    if (args.length >1 )
	    {
		    if (args[1] instanceof Fmessage)
		    	restOfPath += "/show/"+(args[1] as Fmessage).id
		    else if (args[1] instanceof Number)
		    	restOfPath += "/show/"+args[1]	    
	    }    
	    return restOfPath
	}
	static content = {
	}
}