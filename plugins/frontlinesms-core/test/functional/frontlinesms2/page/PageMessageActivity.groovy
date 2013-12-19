package frontlinesms2.page

import frontlinesms2.*

abstract class PageMessageActivity extends frontlinesms2.message.PageMessage {
	static url = 'message/activity'
	String convertToPath(Object[] args) {
		println("Building url...")
		def restOfPath = ""
		if (args.length == 0)
			return ""// will give 'activity not found' & redirect to inbox

		// find the activity
		if (args[0] instanceof Number) {
			restOfPath += '/' + args[0]
		} else {
			restOfPath += '/' + remote { Activity.findByName(args[0])?.id }
		}

	    // find the message
	    if (args.length > 1)
	    {
	    	if (args[1] instanceof TextMessage)
	    		restOfPath +="/show/"+(args[1] as TextMessage).id
	    	else if (args[1] instanceof Number)
	    		restOfPath +="/show/"+args[1]
	    }
	    println("Activity URL::" +url+restOfPath)
	    return restOfPath
	}
	static content = {

	}
}
