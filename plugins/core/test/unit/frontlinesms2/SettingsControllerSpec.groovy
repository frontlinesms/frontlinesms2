package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class SettingsControllerSpec extends ControllerSpec {
	
	def "can view the list of all log entries"() {
		given:
			def entry1 = new LogEntry(date: new Date(), content: "12345")
			def entry2 = new LogEntry(date: new Date()-10, content: "message sent")
			mockDomain(LogEntry, [entry1, entry2])
		when:
			mockParams.timePeriod = 'forever'
			def model = controller.list()
		then:
			model.logEntryList.containsAll(entry1, entry2)
	}
	
	def "can filter list of log entries by time"() {
		given:
			def now = new Date()
			def entry1 = new LogEntry(date: now, content: "entry1")
			def entry2 = new LogEntry(date: now-2, content: "entry2")
			def entry3 = new LogEntry(date: now-6, content: "entry3")
			def entry4 = new LogEntry(date: now-13, content: "entry4")
			def entry5 = new LogEntry(date: now-27, content: "entry5")
			def entry6 = new LogEntry(date: now-100, content: "entry6")
			mockDomain(LogEntry, [entry1, entry2, entry3, entry4, entry5, entry6])
		when:
			mockParams.timePeriod = "1"
			def model = controller.list()
		then:
			model.logEntryList == [entry1]
		when:
			mockParams.timePeriod = "3"
			model = controller.list()
		then:
			model.logEntryList == [entry1, entry2]
		when:
			mockParams.timePeriod = "7"
			model = controller.list()
		then:
			model.logEntryList == [entry1, entry2, entry3]
		when:
			mockParams.timePeriod = "14"
			model = controller.list()
		then:
			model.logEntryList == [entry1, entry2, entry3, entry4]
		when:
			mockParams.timePeriod = "28"
			model = controller.list()
		then:
			model.logEntryList == [entry1, entry2, entry3, entry4, entry5]
		when:
			mockParams.timePeriod = "forever"
			model = controller.list()
		then:
			model.logEntryList == [entry1, entry2, entry3, entry4, entry5, entry6]
		
	}
}

