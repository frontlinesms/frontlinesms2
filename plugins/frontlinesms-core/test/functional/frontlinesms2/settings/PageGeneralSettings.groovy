package frontlinesms2.settings

import frontlinesms2.*

class PageGeneralSettings extends PageSettings {
	static url = 'settings/general'
	
	static content = {
		languageList { $('select#language') }
		errors(required:false) { $('label.error')}
		basicAuthentication {module BasicAuthentication}
	}
	static at = {
		title.contains('Settings') || title.contains('Mazingira')
	}
}

class BasicAuthentication extends geb.Module {
	static base = { $('#basic-authentication') }
	
	static content = {
		authenticationForm { $('form#basic-auth')}
		enableAuthentication { authenticationForm.enableAuthentication}
		username { authenticationForm.username }
		password { authenticationForm.password }
		confirmPassword { authenticationForm.confirmPassword }
		save { $("#basicAuth")}
	}
}