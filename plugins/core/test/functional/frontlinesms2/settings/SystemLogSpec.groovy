package frontlinesms2.settings

import frontlinesms2.*
import spock.lang.*

class SystemLogSpec extends SettingsBaseSpec {

	def 'Can access user support from export logs dialog'() {
		given:
			createTestLogs()
		when:
			to PageLogs
			btnExportLogs.click()
		then:
			waitFor { at SendLogsDialog }
		when:
			userSupportLink.click()
		then:
			$("title").text().contains("FrontlineSMS")
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
