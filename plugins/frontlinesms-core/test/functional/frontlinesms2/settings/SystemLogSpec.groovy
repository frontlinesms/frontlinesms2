package frontlinesms2.settings

import frontlinesms2.*
import spock.lang.*

class SystemLogSpec extends grails.plugin.geb.GebSpec {

	def 'Can access user support from export logs dialog'() {
		given:
			createTestLogs()
		when:
			to PageLogs
			btnExportLogs.click()
		then:
			waitFor { at SendLogsDialog }
			userSupportLink.@href == 'http://community.frontlinesms.com/'
	}

	def createTestLogs() {
		new LogEntry(date:new Date(), content: "Log entry one").save(failOnError:true, flush:true)
	}
}

