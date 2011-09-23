package frontlinesms2.search

import frontlinesms2.*

class CheckSearchMessageSpec extends SearchGebSpec {
	
	def "header checkbox is checked when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to SearchPage
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
			to SearchPage
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { checkedMessageCount == 2}
	}
	
	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to SearchPage
			messagesSelect[2].click()
		then:
			waitFor { $("#message-details #contact-name").text() == 'Alice' }
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
			new Contact(name: 'Alice', primaryMobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			to SearchPage
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { replyToMultipleButton.displayed }
		when:
			replyToMultipleButton.click() // click the reply button
			$("div#tabs-1 .next").click()
		then:
			waitFor { $('input', value:'Alice').checked }
			$('input', value:'Bob').checked
			!$('input', value:'June').checked
	}
	
	def "'Forward' button still work when all messages are unchecked"() {
		given:
			createInboxTestMessages()
		when:
			to SearchPage
			messagesSelect[0].click()
		then:
			waitFor { checkedMessageCount == 3 }
		when:
			messagesSelect[0].click()
		then:
			waitFor { checkedMessageCount == 0 }
		when:
			$('#btn_dropdown').click()
		then:
			waitFor { $('#btn_forward').displayed }
		when:
			$('#btn_forward').click()
		then:
			waitFor { $('textArea', name:'messageText').text() == "i like chicken" }
	}
	
	def "should set row as selected when a message is checked"() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Bob')
		when:
			to SearchPage
			messagesSelect[2].click()
		then:
			waitFor { messagesSelect[2].checked }
			messagesSelect[2].parent().parent().hasClass("selected")
	}


	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			new Fmessage(src: "src", dst: "dst", status: MessageStatus.INBOUND).save(flush: true)
		when:
			to SearchPage
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
	
	def "can archive multiple messages where any owned messages are ignored, but those that are not are archived"() {
		given:
			createInboxTestMessages()
		when:
			to SearchForIPage
			messagesSelect[0].click()
		then:
			waitFor { archiveAllButton.displayed }
		when:
			archiveAllButton.click()
		then:
			waitFor { $('title').text() == "Results"}
	}
}

class SearchForIPage extends SearchPage {
	static def url = "search/result?searchString=i"
}
