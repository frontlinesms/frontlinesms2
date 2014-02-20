package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.message.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import static frontlinesms.grails.test.EchoMessageSource.parseDate

class PageMessageTrash extends PageMessage {
	static url = 'message/trash'
	static at = {
		title.endsWith('Trash')
	}

	String convertToPath(Object[] args) {
		args? "/show/"+args[0]: ''
	}

	static content = {
		trashMoreActions { $("ul.buttons li.trash #trash-actions") }
		senderDetails { $('div#message-info p#interaction-detail-sender').text() }
		date {
			parseDate($('div#message-info p#interaction-detail-date').text())
		}
	}
}

