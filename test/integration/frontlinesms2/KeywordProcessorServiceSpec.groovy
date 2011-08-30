package frontlinesms2

import frontlinesms2.*
import spock.lang.*
import grails.plugin.spock.*

class KeywordProcessorServiceSpec extends UnitSpec {
	def service = new KeywordProcessorService()
	
	def "test matches"() {
		given:
/*			mockDomain(Poll)*/
			def response1 = new PollResponse(value:"Barcelona")
			def response2 = new PollResponse(value:"Manchester Utd")
/*			mockDomain(PollResponse, [response1, response2])*/
			def p = new Poll(title:"football")
			p.addToResponses(response1)
			p.addToResponses(response2)
			p.save(failOnError:true)
			
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

