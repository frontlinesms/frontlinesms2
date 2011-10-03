package frontlinesms2.contact

import frontlinesms2.*

class PageContact extends geb.Page {
	static url = 'contact'
	static content = {
		contactSelect(required:false) { $(".contact-select") }
		contactCount(required:false) { $('#contact-count') }
		contactGroup(required:false) { $('#multiple-contact')}
		contactForm(required:true) {$('#details')}
	}
}