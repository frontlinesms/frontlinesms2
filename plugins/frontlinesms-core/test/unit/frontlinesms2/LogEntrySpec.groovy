package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

class LogEntrySpec extends Specification {
	private static final Date TEST_DATE = new Date()

	@Unroll
	def "a log entry must have a date and text"() {
		expect:
			new LogEntry(date:date, content:content).validate() == valid
		where:
			valid | date      | content
			false | null      | null
			false | TEST_DATE | null
			false | null      | "This log entry now has content"
			true  | TEST_DATE | "This log entry now has content"
	}
}

