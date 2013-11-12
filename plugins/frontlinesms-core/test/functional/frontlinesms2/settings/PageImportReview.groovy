package frontlinesms2.settings

import frontlinesms2.*

class PageImportReview extends PageSettings {
	static url = 'settings/porting'
	
	static content = {
		valueAt { x, y ->
			println "## Trying to retrieve value of cell at position $x, $y"
			$("input", 'data-x': "$x", 'data-y': "$y").value()
		}
		submitButton { $("#submitContacts") }
	}
	static at = {
		title.contains('settings.import.contact.review.page.header')
	}
}

