package frontlinesms2.message

import frontlinesms2.*

class MessageActionSpec extends frontlinesms2.poll.PollGebSpec {
	def 'message actions menu is displayed for all individual messages'() {
		given:
			createTestPolls()
			createTestMessages()
			createTestFolders()
		when:
			to PollMessageViewPage
			def actions = $('#move-actions').children()*.text()
		then:
			actions[1] == "Shampoo Brands"

		when:
			go "message/inbox/show/${Fmessage.findBySrc("Bob").id}"
			def inboxActions = $('#move-actions').children()*.text()
		then:
			inboxActions[1] == "Football Teams"
	}
	
	def 'clicking on poll moves multiple messages to that poll and removes it from the previous poll or inbox'() {
		given:
			createTestPolls()
			createTestMessages()
			def bob = Fmessage.findBySrc('Bob')
			def alice = Fmessage.findBySrc('Alice')
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
		when:
			to PollMessageViewPage
			messagesSelect[0].click()
		then:
			waitFor { $('#move-actions').displayed }
		when:
			$('#move-actions').jquery.val('2') // bug selecting option - seems to be solved by using jquery...
			$('#move-actions').jquery.trigger('change') // again this should not be necessary, but works around apparent bugs
		then:
			waitFor { $('#no-messages').displayed }
			!(bob in footballPoll.messages)
			!(alice in footballPoll.messages)
			bob in shampooPoll.messages
			alice in shampooPoll.messages
	}
	
	def "archive action should not be available for messages that belongs to a message owner  such as activities"() {
		setup:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
		then:
			!$("#message-archive").displayed
	}
}

class PollMessageViewPage extends geb.Page {
 	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}" }
	static content = {
		messagesSelect { $(".message-select") }
	}
}
