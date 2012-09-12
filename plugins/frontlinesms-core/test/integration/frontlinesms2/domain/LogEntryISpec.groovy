package frontlinesms2.domain

import frontlinesms2.*

class LogEntryISpec extends grails.plugin.spock.IntegrationSpec {

	def "calling log on LogEntry creates a log entry"() {
		when:
			LogEntry.log("some log of something")
		then:
			LogEntry.findByContent("some log of something")
	}
}
