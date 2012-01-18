package frontlinesms2.contact

import frontlinesms2.*

class PageContactCreateGroup extends geb.Page {
	static url = 'contact'
	static at = {
		title.endsWith('Create Group')
	}
	
	static content = {
		errorMessages { $('.flash .message') }
	}
}