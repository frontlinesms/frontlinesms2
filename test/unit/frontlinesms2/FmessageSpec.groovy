package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FmessageSpec extends UnitSpec {
	def 'check that READ flag cannot be null'() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			Fmessage message = new Fmessage(read: null, date: new Date())
		then:
			message.read != null || !message.validate()
	}
	
	def 'check that messages are unread by default'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.read == false
	}
	
	def 'messages are unstarred by default'() {
		when:
			Fmessage message = new Fmessage()
		then:
			message.starred == false
	}

	def "Fmessage can have null status"() {
		setup:
			mockDomain(Fmessage)
		when:
			def m = new Fmessage(status: null,  date: new Date())
		then:
			m.validate()
	}
	
	def "Fmessage must have a date"() {
		setup:
			mockForConstraintsTests(Fmessage)
		when:
			def m = new Fmessage()
		then:
			!m.validate()
		when:
			def f = new Fmessage(date: new Date())
		then:
			f.validate()
	} 
}

