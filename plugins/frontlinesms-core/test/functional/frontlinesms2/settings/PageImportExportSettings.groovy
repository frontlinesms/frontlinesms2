package frontlinesms2.settings

import frontlinesms2.*

class PageImportExportSettings extends PageSettings {
	static url = 'settings/porting'
	
	static content = {
		exportOption { option-> $("input[name=exportData]", value:option) }
		exportButton { $("input#exportSubmit") }
		importFileUploader { $("input[name=contactImportFile]") }
		uploadCsv { path ->
			$("form[name=importForm]").contactImportFile = path
			return true
		}
		importOption { option-> $("input[name=data]", value:option) }
	}
	static at = {
		title.contains('settings')
	}
}

