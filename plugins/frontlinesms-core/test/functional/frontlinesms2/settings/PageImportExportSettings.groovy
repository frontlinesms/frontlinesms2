package frontlinesms2.settings

import frontlinesms2.*

class PageImportExportSettings extends PageSettings {
	static url = 'settings/porting'
	
	static content = {
		exportOption { option-> $("input[name=exportData]", value:option) }
		exportButton { $("input#exportSubmit")}
	}
	static at = {
		title.contains('Settings') || title.contains('Mazingira')
	}
}
