package frontlinesms2.controller

import frontlinesms2.*

class MessageActionsISpec extends grails.plugin.spock.IntegrationSpec {
	
	def controller
	
	def setup() {
		controller = new MessageController()
	}
	
	def "message can be moved to a different poll response"() {
		setup:
			def r = new PollResponse(value:'known unknown')
			def r2 = new PollResponse(value:'unknown unknown')
			def poll = new Poll(name: 'Who is badder?')
			poll.addToResponses(r2)
			poll.addToResponses(r).save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save(failOnError:true, flush:true)
			PollResponse.findByValue('known unknown').addToMessages(Fmessage.findBySrc('Bob'))
			poll.save(failOnError: true)
		when:
			controller.params.messageIdList = ',' + message.id + ','
			controller.params.responseId = r2.id
			controller.params.ownerId = poll.id
			controller.changeResponse()
		then:
			r2.messages.contains(message)
	}
	
	def "message can be moved to a folder"() {
		setup:
			def folder = new Folder(name: 'nairobi').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save(failOnError: true, flush:true)
		when:
			controller.params.messageId = ',' + message.id + ','
			controller.params.ownerId = folder.id
			controller.params.messageSection = 'folder'
			controller.move()
		then:
			folder.getFolderMessages([:]).find {message}
			message.messageOwner == folder
	}

	def "should move a folder message to inbox section"() {
		setup:
			def folder = new Folder(name: 'nairobi').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', text:'I like nairobi', inbound:true, date: new Date()).save(failOnError: true, flush:true)
			folder.addToMessages(message)
			folder.save(failOnError:true, flush:true)
		when:
			assert message.messageOwner
			controller.params.messageId = ',' + message.id + ','
			controller.params.messageSection = 'inbox'
			controller.move()
		then:
			!message.messageOwner
			message.inbound
	}

	def "should move a poll message to inbox section"() {
		setup:
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA: 'known unknown', choiceB: 'unknown unknowns').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save(failOnError:true, flush:true)
			PollResponse.findByValue('known unknown').addToMessages(message)
			poll.save(failOnError: true)
		when:
			assert message.messageOwner
			controller.params.messageId = ',' + message.id + ','
			controller.params.messageSection = 'inbox'
			controller.move()
		then:
			!message.messageOwner
			message.inbound
	}
}
