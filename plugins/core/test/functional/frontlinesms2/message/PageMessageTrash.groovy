package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.message.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class PageMessageTrash extends PageMessage {
	static url = 'message/trash'
	static at = {
		title.endsWith('Trash')
	}

	String convertToPath(Object[] args) {
		def restOfPath = ""

		if (args.equals(null) || args.length == 0)
			return ""
	    else
		    restOfPath += "/show/"+args[0]	       
	    	return restOfPath
	}

	static content = {
		trashMoreActions { $("ul.buttons li.trash #trash-actions") }
		senderDetails { $('div#message-info p#message-detail-sender').text() }
		date { 
			new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US)
				.parse($('div#message-info p#message-detail-date').text())
			 }
	}
}