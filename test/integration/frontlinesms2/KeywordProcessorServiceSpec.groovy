package frontlinesms2

import frontlinesms2.*
import spock.lang.*
import grails.plugin.spock.*

class KeywordProcessorServiceSpec extends IntegrationSpec {
	def service = new KeywordProcessorService()
	
	def "test getPollResponse"() {
		given:
			def response1 = new PollResponse(value:"AC Milan")
			def response2 = new PollResponse(value:"FC United of Manchester")
			def p = new Poll(title:'Who is the best football team in the world?', keyword:"football")
			p.addToResponses(response1)
			p.addToResponses(response2)
			p.save(failOnError:true)
			
			def matchingMessageTexts = ['footballa', 'football a', ' football a', ' footballa   ', '''football
   a''', 'FOOTBALL A', 'foOTBaLLa']
		when:
			def results = matchingMessageTexts.collect {
				service.getPollResponse(it)
			}
		then:
			results.each { assert it == response1 }
		
		when:
			matchingMessageTexts = matchingMessageTexts.collect {
				it.reverse().replaceFirst("[a|A]", "b").reverse()
			}
			results = matchingMessageTexts.collect {
				service.getPollResponse(it)
			}
		then:
			results.each { assert it == response2 }
	}
	
	def "processPollResponse() should associate Fmessage with PollResponse"() {
		given:
			Fmessage m = new Fmessage().save(failOnError: true)
			def p = new Poll(title:'Who is the best football team in the world?')
			PollResponse r = new PollResponse(value: "whatever")
			PollResponse r2 = new PollResponse(value: "2")
			p.addToResponses(r2)
			p.addToResponses(r).save(failOnError: true)
		when:
			service.processPollResponse(r, m)
		then:
			r.messages?.size() == 1
	}
}

