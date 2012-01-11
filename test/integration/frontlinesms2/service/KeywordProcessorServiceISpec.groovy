package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*
import grails.plugin.spock.*

class KeywordProcessorServiceISpec extends IntegrationSpec {
	def keywordProcessorService
//	def service = new KeywordProcessorService()
	
	def "test getPollResponse"() {
		given:
			def response1 = new PollResponse(value:"AC Milan")
			def response2 = new PollResponse(value:"FC United of Manchester")
			def p = new Poll(title:'Who is the best football team in the world?', keyword:"football")
			p.addToResponses(response1)
			p.addToResponses(response2)
			p.save(failOnError:true)
		when:
			def matchingMessageTexts = ['footballa', 'football a', ' football a', ' footballa   ', '''football
   a''', 'FOOTBALL A', 'foOTBaLLa']
		then:
			getResults(matchingMessageTexts).every { it == response1 }
		
		when:
			matchingMessageTexts = matchingMessageTexts.collect {
				it.reverse().replaceFirst("[a|A]", "b").reverse()
			}
		then:
			getResults(matchingMessageTexts).every { it == response2 }
		when:
			def nonMatchingMessageTexts = ['', 'f', 'footbal', 'footbal a', 'football', 'a football']
		then:
			getResults(nonMatchingMessageTexts).every { it == null }
	}
	
	def "processPollResponse() should associate Fmessage with PollResponse"() {
		given:
			Fmessage m = new Fmessage(date: new Date()).save(failOnError: true)
			def p = new Poll(title:'Who is the best football team in the world?')
			PollResponse r = new PollResponse(value: "whatever")
			PollResponse r2 = new PollResponse(value: "2")
			p.addToResponses(r2)
			p.addToResponses(r).save(failOnError: true)
		when:
			keywordProcessorService.processPollResponse(r, m)
		then:
			r.messages?.size() == 1
	}
	
	def "processPollResponse() should send reply text for a poll requiring autoreply"() {
		given:
			def m = new Fmessage(src:"0722334455", date: new Date()).save(failOnError: true)
			def p = new Poll(title:'Who is the best football team in the world?', autoReplyText:"Thank you for participating in this poll")
			PollResponse r = new PollResponse(value: "whatever")
			PollResponse r2 = new PollResponse(value: "2")
			p.addToResponses(r2)
			p.addToResponses(r).save(failOnError: true)
		when:
			keywordProcessorService.processPollResponse(r, m)
		then:
			Fmessage.findByText("Thank you for participating in this poll")
	}	
	
	def "processPollResponse() should not send reply text for a poll without autoreply"() {
		given:
			Fmessage m = new Fmessage(src:"0722334455", date: new Date()).save(failOnError: true)
			def p = new Poll(title:'Who is the best football team in the world?')
			PollResponse r = new PollResponse(value: "whatever")
			PollResponse r2 = new PollResponse(value: "2")
			p.addToResponses(r2)
			p.addToResponses(r).save(failOnError: true)
		when:
			keywordProcessorService.processPollResponse(r, m)
		then:
			Fmessage.count() == 1
	}
	
	private def getResults(messageTexts) {
		messageTexts.collect {
			keywordProcessorService.getPollResponse(it)
		}
	}
}

