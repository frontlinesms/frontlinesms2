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
		when:
			to PollMessageViewPage
			def bob = Fmessage.findBySrc('Bob')
			def alice = Fmessage.findBySrc('Alice')
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
			$("#message")[0].click()
			sleep 1000
			moveTo(Poll.findByTitle('Shampoo Brands').id.toString())
			sleep 1000
			shampooPoll.responses.each{ it.refresh() }
			footballPoll.responses.each{ it.refresh() }
		then:
			bob != Poll.findByTitle("Football Teams").getMessages(['starred':false]).find { it == bob }
			alice != Poll.findByTitle("Football Teams").getMessages(['starred':false]).find { it == alice }
			bob == Poll.findByTitle("Shampoo Brands").getMessages(['starred':false]).find { it == bob }
			alice == Poll.findByTitle("Shampoo Brands").getMessages(['starred':false]).find { it == alice }
	}

	private def moveTo(value) {
		$('#move-actions').getJquery().val(value)
		$('#move-actions').getJquery().trigger("change")
	}
}

class PollMessageViewPage extends geb.Page {
 	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}" }
}
