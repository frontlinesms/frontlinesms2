package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowBob extends PageContactShow {
	static url = "contact/show/${Contact.findByName('Bob').id}"
	static content = {
	}
}
