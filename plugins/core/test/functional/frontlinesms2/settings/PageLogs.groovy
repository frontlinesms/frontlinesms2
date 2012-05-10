package frontlinesms2.settings

import frontlinesms2.*

class PageLogs extends geb.Page {
	static url = 'settings/logs'
	static at = {
		title.contains('Settings')
	}

	static content = {
		logDates { $('select', name:'timePeriod') }
		btnExportLogs { $('a', id:'downloadLogs' )  }
	}
}
