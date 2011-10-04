package frontlinesms2.controller

import spock.lang.*
import frontlinesms2.*
import grails.plugin.spock.*

class DeleteISpec extends IntegrationSpec {
	def "deleted polls are not included in the pollInstanceList"() {
		given:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save()
			def message2 = new Fmessage(src:'Alice', dst:'+2541234567', text:'go barcelona', status:MessageStatus.INBOUND).save()
			def p = Poll.createPoll(title: 'This is a poll', choiceA: 'Manchester', choiceB:'Barcelona').save(failOnError:true, flush:true)
			def messageController = new MessageController()
			def pollController = new PollController()
			PollResponse.findByValue('Manchester').addToMessages(message1).save(failOnError: true)
			PollResponse.findByValue('Barcelona').addToMessages(message2).save(failOnError: true)
			p.save(flush:true, failOnError:true)
		when: "model is returned"
			messageController.beforeInterceptor()
			def model1 = messageController.getShowModel()
		then: "it contains one poll"
			model1.pollInstanceList == [p]
		when: "poll is deleted"
			pollController.params.id = p.id
			pollController.delete()
			messageController.beforeInterceptor()
			def model2 = messageController.getShowModel()
		then: "poll instance list is empty"
			!model2.pollInstanceList
	}
	
	def "deleted polls are not included in the polls list"() {
		given:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save()
			def message2 = new Fmessage(src:'Alice', dst:'+2541234567', text:'go barcelona', status:MessageStatus.INBOUND).save()
			def p = Poll.createPoll(title: 'This is a poll', choiceA: 'Manchester', choiceB:'Barcelona').save(failOnError:true, flush:true)
			def pollController = new PollController()
			PollResponse.findByValue('Manchester').addToMessages(message1).save(failOnError: true)
			PollResponse.findByValue('Barcelona').addToMessages(message2).save(failOnError: true)
			p.save(flush:true, failOnError:true)
		when: "model is returned"
			pollController.params.viewingArchive = false
			def model1 = pollController.index()
		then: "it contains one poll"
			model1.polls == [p]
		when: "poll is deleted"
			pollController.params.id = p.id
			pollController.delete()
			pollController.params.viewingArchive = false
			def model2 = pollController.index()
		then: "polls list is empty"
			!model2.polls
	}
}

