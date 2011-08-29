package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.plugin.spock.*

class KeywordProcessorServiceSpec extends UnitSpec {
	def service = new KeywordProcessorService()
	
	def "test matches"() {
		given:
			mockDomain(Poll)
			def response = new PollResponse()
			mockDomain(PollResponse, [response])
			def p = new Poll(title:"Football")
			p.addToResponses(response)
			p.save()
			
			// TODO create football poll
			def matchingMessageTexts = ['footballa', 'football a', ' football a', ' footballa   ', '''football
   a''', 'FOOTBALL A', 'foOTBaLLa'] // TODO add some difference cases
		when:
			def results = matchingMessageTexts.collect {
				service.matches(it)
			}
		then:
			results.each { assert it }
	}
}

