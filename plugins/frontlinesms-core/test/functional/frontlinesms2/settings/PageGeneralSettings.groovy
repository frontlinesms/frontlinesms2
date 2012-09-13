package frontlinesms2.settings

import frontlinesms2.*

class PageGeneralSettings extends PageSettings {
	static url = 'settings/general'
	
	static content = {
		languageList { $('select#language') }
	}
	static at = {
		title.contains('Settings') || title.contains('Mazingira')
	}
}