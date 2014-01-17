package frontlinesms2.message

import frontlinesms2.*

class PageMessageInboxBob extends PageMessageInbox {
	static url =  "message/inbox/show/${TextMessage.findBySrc('Bob').id}"
}
