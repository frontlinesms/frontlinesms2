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
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
			messageList.toggleSelect(2)
		then:
			waitFor { messageList.selectAll.checked }
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { messageList.selectedMessageCount() == 2}
	}
	
	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelect(2)
		then:
			waitFor { singleMessageDetails.text == 'hi Bob' }
		when:
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
			messageList.hasClass(1, "selected")
			messageList.hasClass(2, "selected")
	}

	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			remote {
				Contact.build(name:'Alice', mobile:'Alice')
				Contact.build(name:'June', mobile:'+254778899')
				null
			}
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.replyAll.displayed }
		when:
			multipleMessageDetails.replyAll.click()
		then:
			waitFor("veryslow") { at QuickMessageDialog }
			waitFor("veryslow") { textArea.displayed }
	}

	def "'Forward' button still work when all messages are unchecked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { messageList.selectedMessageCount() == 2 }
		when:
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		then:
			waitFor { singleMessageDetails.text == "hi Alice" }
		when:
			singleMessageDetails.forward.click()
		then:
			waitFor("veryslow") { at QuickMessageDialog }
			waitFor("veryslow") { textArea.jquery.val() == "hi Alice" }
	}
	
	def "should set row as selected when a message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResult, "hi"
			messageList.toggleSelect(1)
		then:
			waitFor { messageList.hasClass(1, "selected") }
	}


	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			remote { TextMessage.build(); null }
		when:
			to PageSearchResult, "hi"
			messageList.selectAll.click()
		then:
			waitFor { messageList.selectedMessageCount() == 3 }
		when:
			messageList.toggleSelect(1)
		then:
			waitFor { messageList.selectedMessageCount() == 2 }
		when:
			messageList.toggleSelect(2)
		then:
			waitFor { messageList.selectedMessageCount() == 1 }
	}
}

