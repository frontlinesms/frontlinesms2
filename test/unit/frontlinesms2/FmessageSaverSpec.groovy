package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FmessageSaverSpec extends UnitSpec {
	@Shared
	def s

	def setupSpec() {
		s = new FmessageSaver()
	}

	def "it's a processor"() {
		expect:
			t instanceof org.apache.camel.Processor
	}

	def "it saves the incoming Fmessage"() {
		given:
			mockDomain([])
			def m = new Fmessage()
		when:
			s.process(createExchange(m))
		then:
			Fmessage.findAll() == [m]
	}
}

