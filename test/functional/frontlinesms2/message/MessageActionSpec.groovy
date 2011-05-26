package frontlinesms2.message

import frontlinesms2.*

class MessageActionSpec extends frontlinesms2.poll.PollGebSpec {
	def 'message actions menu is displayed for all individual messages'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def actions = $('#message-actions li').children('a')*.text()
		then:
			actions[0] == 'Shampoo Brands'

		when:
			go "message/inbox/show/${Fmessage.findBySrc("Bob").id}"
			def inboxActions = $('#message-actions li a')*.text()
		then:
			inboxActions[0] == 'Football Teams'
		cleanup:
			deleteTestPolls()

	}
	
	def 'clicking on activity moves the message to that activity and removes it from the previous activity or inbox'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def btnAction = $('#message-actions li').children('a').first()
			def bob = Fmessage.findBySrc('Bob')
			def jill = Fmessage.findBySrc('Jill')
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
			btnAction.click()
			shampooPoll.responses.each{ it.refresh() }
			footballPoll.responses.each{ it.refresh() }
		then:
			bob != Poll.findByTitle("Football Teams").getMessages().find { it == bob }
			bob == Poll.findByTitle("Shampoo Brands").getMessages().find { it == bob }

		when:
			go "message/inbox/show/${jill.id}"
			btnAction = $('#message-actions li').children('a').first()
			btnAction.click()
			footballPoll.responses.each { it.refresh() }
			Fmessage.findAll().each { it.refresh() }
		then:
			jill != Fmessage.getInboxMessages().find { it == jill }
			jill == Poll.findByTitle("Football Teams").getMessages().find { it == jill }
		cleanup:
			deleteTestPolls()
	}
	
}
class PollMessageViewPage extends geb.Page {
 	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}" }
}
