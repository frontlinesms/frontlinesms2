package frontlinesms2.message

import frontlinesms2.*

abstract class PageMessageActivity extends PageMessage {
	static url = 'message/activity'
	String convertToPath(Object[] args) {
		println("Building url...")
		def restOfPath = ""
		if (args.length == 0)
			return ""// will give 'activity not found' & redirect to inbox

		// find the activity
	    if (args[0] instanceof Activity)
	    	restOfPath += "/"+(args[0] as Activity).id
	    else if (args[0] instanceof Number)
	    	restOfPath += "/"+args[0]
	    else
	    	restOfPath += "/"+Activity.findByName(args[0])?.id

	    // find the message
	    if (args.length > 1)
	    {
	    	if (args[1] instanceof Fmessage)
	    		restOfPath +="/show/"+(args[1] as Fmessage).id
	    	else if (args[1] instanceof Number)
	    		restOfPath +="/show/"+args[1]
	    }
	    println("Activity URL::" +url+restOfPath)
	    return restOfPath
	}
	static content = {

	}
}