package frontlinesms2.search

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

class SearchCheckSpec extends SearchBaseSpec {
	
	def "header checkbox is checked when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResult, "hi"
			waitFor("veryslow") { messageList.displayed }
			messageList.toggleSelected(0)
			messageList.toggleSelected(1)
			messageList.toggleSelected(2)
		then:
			waitFor { messageList.selectAll.checked }
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelected(0)
			messageList.toggleSelected(1)
		then:
			waitFor { messageList.selectedMessages.size() == 2}
	}
	
	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelected(2)
		then:
			waitFor { singleMessageDetails.text == 'hi Bob' }
		when:
			messageList.toggleSelected(1)
		then:
			waitFor { multipleMessageDetails.displayed }
			messageList.messages[1].hasClass("selected")
			messageList.messages[2].hasClass("selected")
	}

	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			Contact.build(name:'Alice', mobile:'Alice')
			Contact.build(name:'June', mobile:'+254778899')
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelected(0)
			messageList.toggleSelected(1)
		then:
			waitFor { multipleMessageDetails.replyAll.displayed }
		when:
			multipleMessageDetails.replyAll.click()
		then:
			waitFor("veryslow") { at QuickMessageDialog }
			waitFor("veryslow") { compose.textArea.displayed }
	}

	def "'Forward' button still work when all messages are unchecked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelected(0)
			messageList.toggleSelected(1)
		then:
			waitFor { messageList.selectedMessages.size() == 2 }
		when:
			messageList.toggleSelected(0)
			messageList.toggleSelected(1)
		then:
			waitFor { singleMessageDetails.text == "hi Alice" }
		when:
			singleMessageDetails.forward.click()
		then:
			waitFor("veryslow") { at QuickMessageDialog }
			waitFor("veryslow") { compose.textArea.jquery.val() == "hi Alice" }
	}
	
	def "should set row as selected when a message is checked"() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Bob')
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelected(1)
		then:
			waitFor { messageList.messages[1].hasClass("selected") }
	}


	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			Fmessage.build()
		when:
			to PageSearchResult, "hi"
			messageList.selectAll.click()
		then:
			waitFor { messageList.selectedMessages.size() == 3 }
		when:
			messageList.toggleSelected(1)
		then:
			waitFor { messageList.selectedMessages.size() == 2 }
		when:
			messageList.toggleSelected(2)
		then:
			waitFor { messageList.selectedMessages.size() == 1 }
	}
}
