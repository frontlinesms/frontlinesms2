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
			actions[1] == "Inbox"
			actions[2] == "Shampoo Brands"
			actions.every {it != "Football Teams"}
		when:
			go "message/inbox/show/${Fmessage.findBySrc("Bob").id}"
			def inboxActions = $('#move-actions').children()*.text()
		then:
			inboxActions[1] == "Football Teams"
			inboxActions.every {it != "Inbox"}
	}

	def "move to inbox option should be displayed for folder messages and should work"() {
		given:
			createTestFolders()
			Folder.findByName("Work").addToMessages(new Fmessage(src: "src", dst: "dst")).save(flush: true)
		when:
			go "message/folder/${Folder.findByName("Work").id}"
			waitFor {title == "Folder"}
			moveTo("inbox")
			sleep 1000
			waitFor {$("div.flash").displayed}
		then:
			$("div.flash").text()
		when:
			$("a", text: "Inbox").click()
			waitFor {title == "Inbox"}
		then:
			$("tbody tr").size() == 1
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
			waitFor {$("div.flash").displayed}
		then:
			Poll.findByTitle("Football Teams").getMessages(['starred':false]).size() == 0
			Poll.findByTitle("Shampoo Brands").getMessages(['starred':false]).size() == 3
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

	def "should move poll messages to inbox"(){
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			$("#message")[0].click()
			sleep 1000
			moveTo("inbox")
			sleep 1000
		then:
			$("div.flash").text()
		when:
			$("a", text: "Inbox").click()
			waitFor {title == "Inbox"}
		then:
			$("tbody tr").size() == 3
	}

	private def moveTo(value) {
		$('#move-actions').getJquery().val(value)
		$('#move-actions').getJquery().trigger("change")
	}
}

class PollMessageViewPage extends geb.Page {
 	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}" }
}
