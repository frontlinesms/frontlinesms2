package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowAlice extends PageContactShow {
	static url = "contact/show/${Contact.findByName('Alice').id}"
	static content = {
	}
}