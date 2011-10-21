package frontlinesms2.message

import frontlinesms2.*

class PageMessageInboxBob extends PageMessageInbox {
	static url =  "message/inbox/show/${Contact.findByName('Bob').id}"
}
