package frontlinesms2.settings

import frontlinesms2.*

class PageGeneralSettings extends PageSettings {
	static url = 'settings/general'
	
	static content = {
		languageList { $('select#language') }
		setLanguage { newLang ->
			def newVal = $('option', text:newLang).@value
			languageList.jquery.val(newVal)
			languageList.jquery.trigger('change')
		}
		errors(required:false) { $('label.error')}
		basicAuthentication {module BasicAuthentication}
		databaseBackup {module DatabaseBackup}
		routing { module RoutingConnections}
	}
	static at = {
		title.contains('Settings') || title.contains('Mazingira')
	}
}

class DatabaseBackup extends geb.Module {
	static base = { $('#database-backup') }

	static content = {
		title { $("h2").text()?.toLowerCase() }
		instruction { $("p").text()?.toLowerCase() }
	}
}

class BasicAuthentication extends geb.Module {
	static base = { $('#basic-authentication') }
	
	static content = {
		authenticationForm { $('form#basic-auth')}
		enabledAuthentication { $("input#enabledAuthentication")}
		username { $("input#username")}
		password { $("input#password")}
		confirmPassword { $("input#confirmPassword")}
		save { $("input#save")}
	}
}

class RoutingConnections extends geb.Module {
	static basr = { $('#routing-preferences') }

	static content = {
		routingForm { $('form#routing-form') }
		useLastReceivedConnection { $('#uselastreceiver') }
		useAnyAvailableConnection { $('input[name=otherwise]', value:'any') }
		dontSend { $('input[name=otherwise]', value:'dontsend') }
		save { $("input#saveRoutingDetails") }
	}
}
