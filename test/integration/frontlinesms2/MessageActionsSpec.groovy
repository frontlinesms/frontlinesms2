package frontlinesms2

import frontlinesms2.enums.MessageStatus

class MessageActionsSpec extends grails.plugin.spock.IntegrationSpec {
	
	def controller
	
	def setup() {
		controller = new MessageController()
	}
	
	def "moving message to poll puts it in Unknown response"() {
		setup:
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError: true)
		when:
			controller.params.messageId = message.id
			controller.params.ownerId = poll.id
			controller.params.messageSection = 'poll'
			controller.move()
		then:
			poll.getMessages([:]).find {message}
			message.messageOwner.value == 'Unknown'
	}
	
	def "message can be moved to a different poll response"() {
		setup:
			def r = new PollResponse(value: 'known unknown').save(failOnError:true, flush:true)
			def r2 = new PollResponse(value: 'unknown unknowns').save(failOnError:true, flush:true)
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA: 'known unknown', choiceB: 'unknown unknowns').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError: true)
			PollResponse.findByValue('known unknown').addToMessages(Fmessage.findBySrc('Bob')).save(failOnError: true)
		when:
			controller.params.messageId = message.id
			controller.params.responseId = r2.id
			controller.params.ownerId = poll.id
			controller.changeResponse()
		then:
			message.messageOwner == r2
	}
	
	def "message can be moved to a folder"() {
		setup:
			def folder = new Folder(name: 'nairobi').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError: true)
		when:
			controller.params.messageId = message.id
			controller.params.ownerId = folder.id
			controller.params.messageSection = 'folder'
			controller.move()
		then:
			folder.getFolderMessages([:]).find {message}
			message.messageOwner == folder
	}
}
