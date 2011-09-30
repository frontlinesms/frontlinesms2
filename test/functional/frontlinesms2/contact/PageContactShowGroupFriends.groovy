package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowGroupFriends extends geb.Page {
	static getUrl() { "group/show/${Group.findByName('Friends').id}" }
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		contactsList { $('#contact-list') }
	}
}