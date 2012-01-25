package frontlinesms2.contact

import frontlinesms2.*

class PageContactCreateContact extends geb.Page {
	static url = 'contact/createContact'
	static at = {
		title.endsWith('Create Contact')
	}
	static content = {
		errorMessages { $('.flash.errors') }
	}
}