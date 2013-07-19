package frontlinesms2.settings

import frontlinesms2.*

class PageLogs extends PageSettings {
	static url = 'settings/logs'
	static at = {
		title.contains('Settings')
	}

	static content = {
		logDates { $('select', name:'timePeriod') }
		btnExportLogs { $('a', id:'downloadLogs' )  }
	}
}

class SendLogsDialog extends PageLogs {
	static at = {
		$("#ui-dialog-title-modalBox").text()?.toLowerCase().contains('download logs to send')
	}

	static content = {
		userSupportLink { $('a',id:'supportLink')}
		continueButton { $("#done") }
	}
}

