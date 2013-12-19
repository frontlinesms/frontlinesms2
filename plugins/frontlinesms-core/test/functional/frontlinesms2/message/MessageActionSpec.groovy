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
			to PageMessagePoll, 'Football Teams', remote { TextMessage.findBySrc('Bob').id }
		then:
			def actions = singleMessageDetails.moveActions.sort()
			actions.containsAll(["fmessage.move.to.inbox", "Shampoo Brands"])
			!actions.contains("Football Teams")
		when:
			to PageMessageInbox, remote { TextMessage.findBySrc("Bob").id }
			def inboxActions = singleMessageDetails.moveActions
		then:
			inboxActions.size() >= 5
			inboxActions.every {it != "fmessage.move.to.inbox"}
	}

	def "move to inbox option should be displayed for folder messages and should work"() {
		given:
			createTestFolders()
			remote { Folder.findByName("Work").addToMessages(new TextMessage(text:'', src: "src", inbound: true)).save(flush:true, failOnError:true); null }
		when:
			to PageMessageFolder, "Work", remote { TextMessage.findBySrc("src").id }
			singleMessageDetails.moveTo("inbox")
		then:
			waitFor { notifications.flashMessage.displayed }
		when:
			to PageMessageInbox
		then:
			messageList.messageCount() == 1
	}
	
	def "can categorize poll messages using dropdown"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', remote { TextMessage.findBySrc("Bob").id }
			def barce = "btn-" + remote { PollResponse.findByValue('barcelona').id }
		then:
			remote { TextMessage.findBySrc("Bob").messageOwner == PollResponse.findByValue('manchester').poll }
		when:
			categoriseSingle(remote { PollResponse.findByValue('barcelona').id })
		then:
			waitFor { notifications.flashMessage.displayed }
			remote { TextMessage.findBySrc("Bob").messageOwner == PollResponse.findByValue('barcelona').poll }
	}

	def 'clicking on poll moves multiple messages to that poll and removes it from the previous poll or inbox'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.selectAll.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(remote { Poll.findByName('Shampoo Brands').id })
		then:
			waitFor("veryslow") { messageList.noContent.displayed }
			remote {
				TextMessage.owned(Poll.findByName('Football Teams')).count() == 0
				TextMessage.owned(Poll.findByName('Shampoo Brands')).count() == 3
			}
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
			messageList.toggleSelect(0)
		then:
			waitFor("veryslow") { singleMessageDetails.displayed }
			!singleMessageDetails.archive.displayed
	}

	def "can move poll message to inbox"() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PageMessagePoll, 'Football Teams', remote { TextMessage.findBySrc("Bob").id }
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo('inbox')
		then:
			waitFor { notifications.flashMessageText }
		when:
			bodyMenu.sectionLink("inbox").click()
		then:
			waitFor { title == "message.header.inbox" }
			messageList.messageCount() == 2
	}
}

