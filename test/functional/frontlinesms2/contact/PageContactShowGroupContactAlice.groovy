package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowGroupContactAlice extends PageContactShow {
	static def getUrl() {
		def alice = Contact.findByName('Alice')
		Group g = Group.findByName('Excellent')
		"group/show/${g.id}/contact/show/${alice.id}"
	}
}