package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FmessageSaverSpec extends UnitSpec {
	def s

	setupSpec() {
		s = new FmessageSaver()
	}

	def "it's a processor"() {
		then:
			t instanceof org.apache.camel.processor.Processor
	}

	def "it saves the incoming Fmessage"() {
		given:
			mockDomain([])
			def m = new Fmessage
		when:
			s.process(createExchange(m))
		then:
			Fmessage.findAll() == [m]
	}
}

