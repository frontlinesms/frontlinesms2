package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class MessageActionSpec extends frontlinesms2.poll.PollGebSpec {
	def 'message actions menu is displayed for all individual messages'() {
		given:
			createTestPolls()
			createTestMessages()
			createTestFolders()
		when:
			to PollMessageViewPage
			def actions = $('#message-actions').children()*.text()
		then:
			actions[1] == "Shampoo Brands"

		when:
			go "message/inbox/show/${Fmessage.findBySrc("Bob").id}"
			def inboxActions = $('#message-actions').children()*.text()
		then:
			inboxActions[1] == "Football Teams"
		cleanup:
			deleteTestFolders()
			deleteTestPolls()
			deleteTestMessages()
	}
	
	def 'clicking on poll moves the message to that poll and removes it from the previous poll or inbox'() {	
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def bob = Fmessage.findBySrc('Bob')
			def jill = Fmessage.findBySrc('Jill')
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
			$('#message-actions').value("${Poll.findByTitle('Shampoo Brands').id}")
			shampooPoll.responses.each{ it.refresh() }
			footballPoll.responses.each{ it.refresh() }
			waitFor {$("div.flash.message").displayed}
		then:
			bob != Poll.findByTitle("Football Teams").getMessages(false).find { it == bob }
			bob == Poll.findByTitle("Shampoo Brands").getMessages(false).find { it == bob }

		when:
			go "message/inbox/show/${jill.id}"
			$('#message-actions').value("${Poll.findByTitle('Football Teams').id}")
			footballPoll.responses.each { it.refresh() }
			Fmessage.findAll().each { it.refresh() }
		then:
			jill != Fmessage.getInboxMessages(false).find { it == jill }
			jill == Poll.findByTitle("Football Teams").getMessages(false).find { it == jill }
		cleanup:
			deleteTestFolders()
			deleteTestPolls()
			deleteTestMessages()
	}
	
	def 'messages are always added to the UNKNOWN response of a poll'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def bob = Fmessage.findBySrc('Bob')
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
			def unknownResponse =  Poll.findByTitle("Shampoo Brands").getResponses().find { it.value == 'Unknown'}
			$('#message-actions').value("${Poll.findByTitle('Shampoo Brands').id}")
			shampooPoll.responses.each{ it.refresh() }
			footballPoll.responses.each{ it.refresh() }
			Fmessage.findAll().each { it.refresh() }
		then:
			bob.messageOwner == unknownResponse
		cleanup:
			deleteTestFolders()
			deleteTestMessages()
			deleteTestPolls()
	}

	def 'possible poll responses are shown in action list and can be clicked to reassign a message to a different response'() {
		given:
			createTestPolls()
			createTestMessages()
			assert Fmessage.findBySrc('Bob').messageOwner.value == 'manchester'
		when:
			to PollMessageViewPage
			def btnAssignToBarcelona = $('#poll-actions li:nth-child(3) a')
		then:
			btnAssignToBarcelona.text() == 'barcelona'
		when:
			btnAssignToBarcelona.click()
            waitFor { $("div.flash.message").text().contains("Fmessage") }
			def barceResponse = PollResponse.findByValue('barcelona')
			def footballPoll = Poll.findByTitle('Football Teams')
			def bob = Fmessage.findBySrc('Bob')
			bob.refresh()
		then:
			bob.messageOwner == barceResponse
		cleanup:
			deleteTestFolders()
			deleteTestPolls()
			deleteTestMessages()
	}
	
	def 'clicking on folder moves the message to that folder and removes it from the previous location'() {
		given:
			createTestPolls()
			createTestMessages()
			createTestFolders()
		when:
			def max = new Fmessage(src:'Max', dst:'+254987654', text:'I will be late', status:MessageStatus.INBOUND).save(failOnError:true, flush:true)
			def footballPoll = Poll.findByTitle('Football Teams')
			def unknownResponse = footballPoll.getResponses().find { it.value == 'Unknown'}
			unknownResponse.addToMessages(max).save(failOnError:true, flush:true)
			def workFolder = Folder.findByName('Work')
			go "message/poll/${footballPoll.id}/show/${Fmessage.findBySrc('Max').id}"
			$('#message-actions').value("${Folder.findByName('Work').id}")
			waitFor {$("div.flash.message").displayed}
			footballPoll.responses.each { it.refresh() }
			workFolder.refresh()
		then:
			max != footballPoll.getMessages(false).find { it == max }
			max == workFolder.getFolderMessages(false).find { it == max }
		cleanup:
			deleteTestFolders()
			deleteTestPolls()
			deleteTestMessages()
	}
	
	def 'clicking on poll moves multiple messages to that poll and removes it from inbox'() {
		given:
			createTestPolls()	
			def alice = new Fmessage(src:'Alice', dst:'+2541234567', text:'go manchester', dateReceived: new Date() - 3, status: MessageStatus.INBOUND).save(failOnError:true, flush:true)
			def joe = new Fmessage(src:'Joe', dst:'+254112233', text:'pantene is the best',  dateReceived: new Date() - 2, status: MessageStatus.INBOUND).save(failOnError:true, flush:true)
		when:
			go '/message/inbox'
			$("#message")[0].click()
			$('#message-actions').value("${Poll.findByTitle('Shampoo Brands').id}")
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
			shampooPoll.responses.each{ it.refresh() }
			footballPoll.responses.each{ it.refresh() }
		then:
			alice == Poll.findByTitle("Shampoo Brands").getMessages(false).find { it == alice }
			joe == Poll.findByTitle("Shampoo Brands").getMessages(false).find { it == joe }
			alice != Fmessage.getInboxMessages(false).find { it == alice }
			joe != Fmessage.getInboxMessages(false).find { it == joe }
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}
	
	def 'clicking on poll moves multiple messages to that poll and removes it from the previous poll'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def bob = Fmessage.findBySrc('Bob')
			def jill = Fmessage.findBySrc('Jill')
			def alice = Fmessage.findBySrc('Alice')
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
			$("#message")[0].click()
			
			$('#message-actions').value("${Poll.findByTitle('Shampoo Brands').id}")
			shampooPoll.responses.each{ it.refresh() }
			footballPoll.responses.each{ it.refresh() }
			waitFor {$("div.flash.message").displayed}
		then:
			bob != Poll.findByTitle("Football Teams").getMessages(false).find { it == bob }
			alice != Poll.findByTitle("Football Teams").getMessages(false).find { it == alice }
			bob == Poll.findByTitle("Shampoo Brands").getMessages(false).find { it == bob }
			alice == Poll.findByTitle("Shampoo Brands").getMessages(false).find { it == alice }
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}
}

class PollMessageViewPage extends geb.Page {
 	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}" }
}
