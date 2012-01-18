package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowGroupFriends extends PageContactShow {
	static url = "group/show/${Group.findByName('Friends').id}"
	static content = {
	}
}