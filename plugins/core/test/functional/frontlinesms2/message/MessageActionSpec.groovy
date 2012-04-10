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
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
		then:
			at PageMessagePollFootballTeamsBob
		when:
			def actions = $('select', name: 'move-actions').children()*.value()
		then:
			actions[1] == "inbox"
			actions[2] == "${Poll.findByName("Shampoo Brands").id}"
			!actions.contains("${Poll.findByName("Football Teams").id}")
		when:
			go "message/inbox/show/${Fmessage.findBySrc("Bob").id}"
			def inboxActions = $('#move-actions').children()*.value()
		then:
			inboxActions[1] == "${Poll.findByName("Football Teams").id}"
			inboxActions.every {it != "inbox"}
	}

	def "move to inbox option should be displayed for folder messages and should work"() {
		given:
			createTestFolders()
			Folder.findByName("Work").addToMessages(new Fmessage(src: "src", inbound: true)).save(flush: true, failOnError: true)
		when:
			go "message/folder/${Folder.findByName("Work").id}/show/${Fmessage.findBySrc("src").id}"
			$('#move-actions').jquery.val('inbox') // bug selecting option - seems to be solved by using jquery...
			$('#move-actions').jquery.trigger('change') // again this should not be necessary, but works around apparent bugs
		then:
			waitFor { $("div.flash").displayed }
		when:
			$("a", text: "Inbox").click()
			waitFor {title == "Inbox"}
		then:
			$("#message-list tr").size() == 2
	}
	
	def "can categorize poll messages using dropdown"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
			def barce = "btn-" + PollResponse.findByValue('barcelona').id
		then:
			Fmessage.findBySrc("Bob").messageOwner == PollResponse.findByValue('manchester').poll
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
			Fmessage.findBySrc("Bob").messageOwner == PollResponse.findByValue('barcelona').poll
	}
	
	def 'clicking on poll moves multiple messages to that poll and removes it from the previous poll or inbox'() {
		given:
			createTestPolls()
			createTestMessages()
			def shampooPoll = Poll.findByName('Shampoo Brands')
			def footballPoll = Poll.findByName('Football Teams')
		when:
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
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
			Fmessage.owned(footballPoll).count() == 0
			Fmessage.owned(shampooPoll).count() == 3
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
			go "message/activity/${Poll.findByName('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}"
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


