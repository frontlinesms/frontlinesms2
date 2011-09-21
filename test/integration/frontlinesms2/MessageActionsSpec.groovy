package frontlinesms2


class MessageActionsSpec extends grails.plugin.spock.IntegrationSpec {
	
	def controller
	
	def setup() {
		controller = new MessageController()
	}
	
	def "moving message to poll puts it in Unknown response"() {
		setup:
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError: true, flush:true)
		when:
			controller.params.messageId = ',' + message.id + ','
			controller.params.ownerId = poll.id
			controller.params.messageSection = 'poll'
			controller.move()
		then:
			poll.getPollMessages().list().find {message}
			message.messageOwner.value == 'Unknown'
	}
	
	def "message can be moved to a different poll response"() {
		setup:
			def r = new PollResponse(value:'known unknown')
			def r2 = new PollResponse(value:'unknown unknown')
			def poll = new Poll(title: 'Who is badder?')
			poll.addToResponses(r2)
			poll.addToResponses(r).save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError:true, flush:true)
			PollResponse.findByValue('known unknown').addToMessages(Fmessage.findBySrc('Bob')).save(failOnError: true)
			
		when:
			controller.params.messageId = ',' + message.id + ','
			controller.params.responseId = r2.id
			controller.params.ownerId = poll.id
			controller.changeResponse()
		then:
			message.messageOwner == r2
	}
	
	def "message can be moved to a folder"() {
		setup:
			def folder = new Folder(name: 'nairobi').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError: true, flush:true)
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
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like nairobi', status:MessageStatus.INBOUND).save(failOnError: true, flush:true)
			folder.addToMessages(message)
			folder.save(failOnError:true, flush:true)
		when:
			assert message.messageOwner
			controller.params.messageId = ',' + message.id + ','
			controller.params.messageSection = 'inbox'
			controller.move()
		then:
			!message.messageOwner
			message.status == MessageStatus.INBOUND
	}

	def "should move a poll message to inbox section"() {
		setup:
			Poll.createPoll(title: 'Who is badder?', choiceA: 'known unknown', choiceB: 'unknown unknowns').save(failOnError:true, flush:true)
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save(failOnError:true, flush:true)
			PollResponse.findByValue('known unknown').addToMessages(message).save(failOnError: true)
		when:
			assert message.messageOwner
			controller.params.messageId = ',' + message.id + ','
			controller.params.messageSection = 'inbox'
			controller.move()
		then:
			!message.messageOwner
			message.status == MessageStatus.INBOUND
	}
}
