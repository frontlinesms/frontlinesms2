package frontlinesms2.settings

import frontlinesms2.*

class PageGeneralSettings extends PageSettings {
	static url = 'settings/general'
	
	static content = {
		languageList { $('select#language') }
		basicAuthentication {module BasicAuthentication}
	}
	static at = {
		title.contains('Settings') || title.contains('Mazingira')
	}
}

class BasicAuthentication extends geb.Module {
	static base = { $('#basic-authentication') }
	
	static content = {
		authenticationForm { $('form')}
		enableAuthentication { authenticationForm.enableAuthentication}
		username { authenticationForm.username }
		password { authenticationForm.password }
		save { $("#basicAuth")}
	}
}