package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.poll.PageMessagePollFootballTeamsBob

class MessageActionSpec extends frontlinesms2.poll.PollBaseSpec {
	
	def 'message actions menu is displayed for all individual messages'() {
		given:
			createTestPolls()
			createTestMessages()
			createTestFolders()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			at PageMessagePollFootballTeamsBob
		when:
			def actions = $('#move-actions').children()*.text()
		then:
			actions[1] == "Inbox"
			actions[2] == "Shampoo Brands"
			!actions.contains("Football Teams")
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
			go "message/folder/${Folder.findByName("Work").id}/show/${Fmessage.findBySrc('src').id}"
			$('#move-actions').jquery.val('inbox') // bug selecting option - seems to be solved by using jquery...
			$('#move-actions').jquery.trigger('change') // again this should not be necessary, but works around apparent bugs
		then:
			waitFor { $("div.flash").displayed }
		when:
			$("a", text: "Inbox").click()
			waitFor {title == "Inbox"}
		then:
			$("tbody tr").size() == 1
	}
	
	def "can categorize poll messages using dropdown"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
			def barce = "btn-" + PollResponse.findByValue('barcelona').id
		then:
			Fmessage.findBySrc("Bob").messageOwner == PollResponse.findByValue('manchester')
		when:
			$('#categorise_dropdown').jquery.val(barce)
			$('#categorise_dropdown').jquery.trigger('change')
		then:
			waitFor { $(".flash").displayed }
		when:
			PollResponse.findByValue('manchester').refresh()
			PollResponse.findByValue('barcelona').refresh()
			Fmessage.findBySrc("Bob").refresh()
		then:
			Fmessage.findBySrc("Bob").messageOwner == PollResponse.findByValue('barcelona')
	}
	
	def 'clicking on poll moves multiple messages to that poll and removes it from the previous poll or inbox'() {
		given:
			createTestPolls()
			createTestMessages()
			def shampooPoll = Poll.findByTitle('Shampoo Brands')
			def footballPoll = Poll.findByTitle('Football Teams')
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			at PageMessagePollFootballTeamsBob
		when:
			messagesSelect[0].click()
		then:
			waitFor { $('#multiple-messages').displayed }
		when:
			setMoveActionsValue(shampooPoll.id.toString())
		then:
			waitFor { $('#no-messages').displayed }
			footballPoll.pollMessages.count() == 0
			shampooPoll.pollMessages.count() == 3
	}

	def "archive action should not be available for messages that belongs to a message owner  such as activities"() {
		setup:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePollFootballTeamsBob
		then:
			!$("#message-archive").displayed
	}

	def "can move poll messages to inbox"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			at PageMessagePollFootballTeamsBob
		when:
			messagesSelect[0].click()
		then:
			waitFor { $('#multiple-messages').displayed }
		when:
			setMoveActionsValue('inbox')
		then:
			waitFor { $("div.flash").text() }
		when:
			$("a", text: "Inbox").click()
		then:	
			waitFor { title == "Inbox" }
			$("tbody tr").size() == 3
	}
	
	private def setMoveActionsValue(value) {
		$('#move-actions').jquery.val(value) // bug selecting option - seems to be solved by using jquery...
		$('#move-actions').jquery.trigger('change') // again this should not be necessary, but works around apparent bugs
	}
}


