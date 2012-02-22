package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class LogEntrySpec extends UnitSpec {
	def "a log entry must have a date and text"() {
		setup:
			mockForConstraintsTests(LogEntry)
		when:
			LogEntry l = new LogEntry()
		then:
			!l.validate()
		when:
			l.date = new Date()
		then:
			!l.validate()
		when:
			l.content = "This log entry now has content"
		then:
			l.validate()
	}
}

