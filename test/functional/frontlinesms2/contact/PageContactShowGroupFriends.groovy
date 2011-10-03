package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowGroupFriends extends PageContactShow {
	static getUrl() { "group/show/${Group.findByName('Friends').id}" }
	static content = {
	}
}