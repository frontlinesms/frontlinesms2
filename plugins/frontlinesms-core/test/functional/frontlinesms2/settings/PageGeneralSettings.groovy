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
		errors(required:false) { $('label.error') }
		basicAuthentication { module BasicAuthentication }
		databaseBackup { module DatabaseBackup }
	}
	static at = {
		title.contains('settings.general.header')
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
	static base = { $('#basic-authentication form#basic-auth') }
	
	static content = {
		save { $('input#submit') }
	}
}

