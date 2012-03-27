package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*
import grails.plugin.spock.*

class KeywordProcessorServiceISpec extends IntegrationSpec {
	def keywordProcessorService
//	def service = new KeywordProcessorService()
	
	def "processForAutoreply() should send reply text"() {
		given:
			def m = new Fmessage(src:"0722334455", date: new Date(), inbound: true).save(failOnError: true, flush: true)
			def keyword = new Keyword(value: "keyword")
			def a = new Autoreply(name:'Who is the best football team in the world?', autoreplyText:"Thank you for sending a message", keyword: keyword)
			a.save(failOnError: true, flush: true)
		when:
			keywordProcessorService.processForAutoreply(keyword, m)
		then:
			Fmessage.findByText("Thank you for sending a message")
	}
	
	def "processForAutoreply() should associate Fmessage with Autoreply"() {
		given:
			def m = new Fmessage(src:"0722334455", date: new Date(), inbound: true).save(failOnError: true, flush: true)
			def keyword = new Keyword(value: "keyword")
			def a = new Autoreply(name:'Who is the best football team in the world?', autoreplyText:"Thank you for participating in this poll", keyword: keyword)
			a.save(failOnError: true, flush: true)
		when:
			keywordProcessorService.processForAutoreply(keyword, m)
		then:
			a.messages?.size() == 2
	}
	
	def "processForPoll() should associate Fmessage with PollResponse"() {
		given:
			def m = new Fmessage(src:"0722334455", date: new Date(), inbound: true).save(failOnError: true, flush: true)
			def keyword = new Keyword(value: "keyword")
			def p = new Poll(name:'Who is the best football team in the world?', autoreplyText:"Thank you for participating in this poll", keyword: keyword)
			p.editResponses('choiceA': "whatever", 'choiceB': "2")
			p.save(failOnError: true, flush: true)
		when:
			keywordProcessorService.processForPoll(keyword, 'A', m)
		then:
			PollResponse.findByValue("whatever").messages?.size() == 1
	}
	
	def "processForPoll() should send reply text for a poll requiring autoreply"() {
		given:
			def m = new Fmessage(src:"0722334455", date: new Date(), inbound: true).save(failOnError: true, flush: true)
			def keyword = new Keyword(value: "keyword")
			def p = new Poll(name:'Who is the best football team in the world?', autoreplyText:"Thank you for participating in this poll", keyword: keyword)
			p.editResponses('choiceA': "whatever", 'choiceB': "2")
			p.save(failOnError: true, flush: true)
		when:
			keywordProcessorService.processForPoll(keyword, 'A', m)
		then:
			Fmessage.findByText("Thank you for participating in this poll")
	}	
	
	def "processForPoll() should not send reply text for a poll without autoreply"() {
		given:
			def m = new Fmessage(src:"0722334455", date: new Date(), inbound: true).save(failOnError: true, flush: true)
			def keyword = new Keyword(value: "keyword")
			def p = new Poll(name:'Who is the best football team in the world?', keyword: keyword)
			p.editResponses('choiceA': "whatever", 'choiceB': "2")
			p.save(failOnError: true, flush: true)
		when:
			keywordProcessorService.processForPoll(keyword, 'A', m)
		then:
			Fmessage.count() == 1
	}
	
	private def getResults(messageTexts) {
		messageTexts.collect {
			keywordProcessorService.getPollResponse(it)
		}
	}
}

