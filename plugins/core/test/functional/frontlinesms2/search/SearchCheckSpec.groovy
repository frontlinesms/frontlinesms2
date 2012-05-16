package frontlinesms2.search

import frontlinesms2.*

class SearchCheckSpec extends SearchBaseSpec {
	
	def "header checkbox is checked when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResultHi
			messagesSelect[1].click()
			messagesSelect[2].click()
			messagesSelect[3].click()
		then:
			waitFor { messagesSelect[0].checked }
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResultHi
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { checkedMessageCount == 2}
	}
	
	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResultHi
			messagesSelect[2].click()
		then:
			waitFor { $("#message-detail #message-detail-sender").text() == 'Alice' }
		when:
			messagesSelect[1].click()
		then:
			waitFor { $('#multiple-messages').displayed }
			messagesSelect[2].parent().parent().hasClass("selected")
			messagesSelect[1].parent().parent().hasClass("selected")
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			Contact.build(name:'Alice', mobile:'Alice')
			Contact.build(name:'June', mobile:'+254778899')
		when:
			to PageSearchResultHi
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { replyToMultipleButton.displayed }
		when:
			replyToMultipleButton.click() // click the reply button
		then:
			waitFor { $("div#tabs-1").displayed }
	}
	
	def "'Forward' button still work when all messages are unchecked"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResultHi
			messagesSelect[1].click()
			messagesSelect[0].click()
		then:
			waitFor { checkedMessageCount == 3 }
		when:
			messagesSelect[0].click()
		then:
			waitFor { $('#message-detail #message-detail-sender').text() == "Barnabus" }
		when:
			$('a', text:'Barnabus').click()	
			$('#btn_forward').click()
		then:
			waitFor { $('textArea', name:'messageText').text() == "i like chicken" }
	}
	
	def "should set row as selected when a message is checked"() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Bob')
		when:
			to PageSearchResultHi
			messagesSelect[2].click()
		then:
			waitFor { messagesSelect[2].checked }
			messagesSelect[2].parent().parent().hasClass("selected")
	}


	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			Fmessage.build()
		when:
			to PageSearchResultHi
			messagesSelect[0].click()
		then:
			waitFor { checkedMessageCount == 3 }
			
		when:
			messagesSelect[1].click()
		then:
			waitFor { checkedMessageCount == 2 }
		when:
			messagesSelect[0].click()
		then:
			waitFor { checkedMessageCount == 3 }
	}
	
	//FIXME this could easily be an integration test
	def "can archive multiple messages where any owned messages are ignored, but those that are not are archived"() {
		given:
			createInboxTestMessages()
		when:
			to PageSearchResultI
			messagesSelect[0].click()
		then:
			waitFor { archiveAllBtn.displayed }
		when:
			archiveAllBtn.jquery.trigger("click")
		then:
			waitFor { $('title').text() == "Results"}
	}
}
