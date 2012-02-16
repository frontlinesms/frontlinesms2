package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowAlice extends PageContactShow {
	static getUrl() { "contact/show/${Contact.findByName('Alice').id}" }
}