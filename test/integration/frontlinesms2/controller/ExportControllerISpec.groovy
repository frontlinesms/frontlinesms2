package frontlinesms2.controller

import frontlinesms2.*

class ExportControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller

	def setup() {
		createTestMessages()
		controller = new ExportController()
	}

	def "should export messages from a poll"() {
		given:
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
		given:
			createTestFolders()
			controller.params.messageSection = "folder"
			controller.params.ownerId = Folder.findByName("Work").id
		when:
			def result = controller.downloadReport()
		then:
			result['messageInstanceList'].size() == 2
	}


	def createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', dateReceived: new Date() - 4, starred: true),
			new Fmessage(src:'Alice', dst:'+2541234567', text:'go manchester', dateReceived: new Date() - 3)].each {
					it.inbound = true
					it.save(failOnError:true, flush:true)
			}
	}

	def createTestFolders() {
		//FIXME: Need to remove.Test fails without this line.
		Folder.list()

		def workFolder = new Folder(name: 'Work')
		workFolder.addToMessages(new Fmessage(src: "Bob", dst: "dst"))
		workFolder.addToMessages(new Fmessage(src: "Alice", dst: "dst"))
		workFolder.save(flush: true)
	}


}
