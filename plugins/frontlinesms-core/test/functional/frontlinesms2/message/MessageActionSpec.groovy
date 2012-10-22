package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.poll.*
import frontlinesms2.folder.*

class MessageActionSpec extends frontlinesms2.poll.PollBaseSpec {

	def 'message actions menu is displayed for all individual messages'() {
		given:
			createTestPolls()
			createTestMessages()
			createTestFolders()
		when:
			to PageMessagePoll, Poll.findByName('Football Teams'), Fmessage.findBySrc('Bob')
		then:
			def actions = singleMessageDetails.moveActions.sort()
			actions[0] == "Inbox"
			actions[4] == "Shampoo Brands"
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
			Folder.findByName("Work").addToMessages(new Fmessage(text:'', src: "src", inbound: true)).save(flush: true, failOnError: true)
		when:
			to PageMessageFolder, Folder.findByName("Work"), Fmessage.findBySrc("src")
			singleMessageDetails.moveTo("inbox")
		then:
			waitFor { notifications.flashMessage.displayed }
		when:
			to PageMessageInbox
		then:
			messageList.messages.size() == 1
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
			to PageMessagePoll, 'Football Teams'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.selectAll.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(shampooPoll.id)
		then:
			waitFor("veryslow") { messageList.noContent.displayed }
			Fmessage.owned(footballPoll).count() == 0
			Fmessage.owned(shampooPoll).count() == 3
	}

	def "archive action should not be available for messages that belongs to a message owner  such as activities"() {
		setup:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor("veryslow") { singleMessageDetails.displayed }
			!singleMessageDetails.archive.displayed
	}

	def "can move poll message to inbox"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', Fmessage.findBySrc("Bob")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages.checkbox[0].click()
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo('inbox')
		then:
			waitFor { notifications.flashMessageText }
		when:
			bodyMenu.messageSection("Inbox").click()
		then:
			waitFor { title == "Inbox" }
			messageList.messages.size() == 2
	}
}


