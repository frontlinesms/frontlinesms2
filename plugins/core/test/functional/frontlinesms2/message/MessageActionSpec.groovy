package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.poll.*
import frontlinesms2.folder.*
import frontlinesms2.poll.PageMessagePollFootballTeamsBob

class MessageActionSpec extends frontlinesms2.poll.PollBaseSpec {

	def 'message actions menu is displayed for all individual messages'() {
		given:
			createTestPolls()
			createTestMessages()
			createTestFolders()
		when:
			to PageMessagePoll, Poll.findByName('Football Teams'), Fmessage.findBySrc('Bob')
		then:
			def actions = singleMessageDetails.moveActions
			actions[1] == "Inbox"
			actions[2] == "Shampoo Brands"
			!actions.contains("Football Teams")
		when:
			to PageMessageInbox, Fmessage.findBySrc("Bob")
			def inboxActions = singleMessageDetails.moveActions
		then:
			inboxActions.size() >= 5
			inboxActions.every {it != "Inbox"}
	}

	def "move to inbox option should be displayed for folder messages and should work"() {
		given:
			createTestFolders()
			Folder.findByName("Work").addToMessages(new Fmessage(src: "src", inbound: true)).save(flush: true, failOnError: true)
		when:
			to PageMessageFolder, Folder.findByName("Work"), Fmessage.findBySrc("src")
			singleMessageDetails.moveTo("inbox")
		then:
			waitFor { notifications.flashMessage.displayed }
		when:
			to PageMessageInbox
		then:
			messageList.messages.size() == 2
	}
	
	def "can categorize poll messages using dropdown"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', Fmessage.findBySrc("Bob")
			def barce = "btn-" + PollResponse.findByValue('barcelona').id
		then:
			Fmessage.findBySrc("Bob").messageOwner == PollResponse.findByValue('manchester').poll
		when:
			categoriseSingle(PollResponse.findByValue('barcelona').id)
		then:
			waitFor { notifications.flashMessage.displayed }
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
			to PageMessagePoll, Poll.findByName('Football Teams'), Fmessage.findBySrc("Bob")
		then:
			at PageMessagePoll
		when:
			messageList.selectAll.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(Poll.findByName('Shampoo Brands'))
		then:
			waitFor { messageList.noContent.displayed }
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
			$("#message-list .main-table tr").size() == 4
	}
	
	private def setMoveActionsValue(value) {
		$('#move-actions').jquery.val(value) // bug selecting option - seems to be solved by using jquery...
		$('#move-actions').jquery.trigger('change') // again this should not be necessary, but works around apparent bugs
	}
}


