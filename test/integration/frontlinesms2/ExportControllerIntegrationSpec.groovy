package frontlinesms2

class ExportControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def controller

	def setup() {
		createTestMessages()
		controller = new ExportController()
	}

	def "should export messages from a poll"() {
		setup:
			Poll.createPoll(title: 'Football Teams', choiceA: 'manchester', choiceB:'barcelona').save(flush: true)
			[PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Bob')),
				PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Alice'))]*.save(failOnError:true, flush:true)
			controller.params.messageSection = "poll"
			controller.params.ownerId = Poll.findByTitle("Football Teams").id
		when:
			def result = controller.downloadReport()
		then:
			result['messageInstanceList'].size() == 2
	}


	def "should export messages from a folder"() {
		setup:
			def folder = new Folder(name: "folder")
			Fmessage.list().each {
				folder.addToMessages(it)
			}
			folder.save(flush: true)
			controller.params.messageSection = "folder"
			controller.params.ownerId = Folder.findByName("folder").id
		when:
			def result = controller.downloadReport()
		then:
			result['messageInstanceList'].size() == 2
	}


	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', dateReceived: new Date() - 4, starred: true),
			new Fmessage(src:'Alice', dst:'+2541234567', text:'go manchester', dateReceived: new Date() - 3)].each {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true, flush:true)
			}

	}

}