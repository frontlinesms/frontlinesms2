package frontlinesms2.message

import frontlinesms2.*

class PageMessageInboxBob extends PageMessageInbox {
	static url =  "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
}
