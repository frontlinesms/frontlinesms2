package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowBob extends PageContactShow {
	static getUrl() { "contact/show/${Contact.findByName('Bob').id}" }
}
