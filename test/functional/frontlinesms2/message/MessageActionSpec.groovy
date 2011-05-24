package frontlinesms2.message

import frontlinesms2.*

class MessageActionSpec extends frontlinesms2.poll.PollGebSpec {
	def 'message actions menu is displayed for all individual messages'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def actions = $('#message-actions li a')*.text()
		then:
			actions[0] == 'Shampoo Brands'

		when:
			go "/messages/inbox/show/${Fmessage.findBySrc("Bob").id}"
			def actions = $('#message-actions li a')*.text()
		then:
			actions[0] == 'Football Teams'
		cleanup:
			deleteTestPolls()

	}
	
	def 'clicking on activity moves the message to that activity and removes it from the previous activity'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def btnAction = $('#message-actions li').children('a').first()
			def bob = Fmessage.findBySrc('Bob')
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
			btnAction.click()
			shampooPoll.responses.each{ it.refresh() }
			footballPoll.responses.each{ it.refresh() }
		then:
			bob == Poll.findByTitle("Shampoo Brands").getMessages().find { it == bob }
			bob != Poll.findByTitle("Football Teams").getMessages().find { it == bob }
		cleanup:
			deleteTestPolls()
	}
	
}
class PollMessageViewPage extends geb.Page {
 	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}" }
}
