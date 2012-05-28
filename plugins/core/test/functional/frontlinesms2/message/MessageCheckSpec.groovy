package frontlinesms2.message

import frontlinesms2.*

import spock.lang.*

class MessageCheckSpec extends MessageBaseSpec {
	
	def "header checkbox is checked when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
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
			to PageMessageInbox
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { checkedMessageCount == 2 }
	}
	
	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.list()[0].id}"
			$(".message-select")[2].click()
		then:
			waitFor { $("#message-detail #message-detail-sender").text() == $(".displayName-${Fmessage.findBySrc('Bob').id}").text() }
		when:
			$(".message-select")[1].click()
		then:
			waitFor { $(".message-select")[1].parent().parent().hasClass("selected") }
			$(".message-select")[2].parent().parent().hasClass("selected")
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			Contact.build(name:'Alice', mobile:'Alice')
			Contact.build(name:'June', mobile:'+254778899')
		when:
			to PageMessageInbox
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $('#multiple-messages a')[0].displayed }
		when:
			$('#multiple-messages a')[0].click()
		then:
			waitFor { $("div#tabs-1").displayed }
	}
	
	def "the count of messages being sent is updated even in 'Reply all'"() {
		given:
			createInboxTestMessages()
			Contact.build(name:'Alice', mobile:'Alice')
			Contact.build(name:'June', mobile:'+254778899')
		when:
			to PageMessageInbox
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $('#multiple-messages a')[0].displayed }
		when:
			$('#multiple-messages a')[0].click()
		then:
			waitFor { $("div#tabs-1").displayed }
		when:
			$('#nextPage').click()
		then:
			$('#messages-count').text() == '2'
	}
	
	def "Should show the correct contact count when replying to multiple checked messages"() {
		given:
			Fmessage.build(src:'Alice', text:'hi Alice')
			Fmessage.build(src:'Alice', text:'test')
			Contact.build(name:'Alice', mobile:'Alice')
		when:
			to PageMessageInbox
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $('#multiple-messages a')[0].displayed }
		when:
			$('#multiple-messages a')[0].click()
		then:
			waitFor { $("#tabs a", text: "Confirm").displayed }
		when:
			$("#tabs a", text: "Confirm").click()
		then:
			waitFor { $('div#tabs-3').displayed }
			$('#recipient').text() == "Alice"
	}
	
	def "Should show the contact's name when replying to multiple messages from the same contact"() {
		given:
			createInboxTestMessages()
			Contact.build(name:'Alice', mobile:'Alice')
			Contact.build(name:'June', mobile:'+254778899')
		when:
			to PageMessageInbox
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $('#multiple-messages a')[0].displayed }
		when:
			$('#multiple-messages a')[0].click()
		then:
			waitFor { $("#tabs a", text: "Confirm").displayed }
		when:
			$("#tabs a", text: "Confirm").click()
		then:
			waitFor { $('div#tabs-3').displayed }
			$('#confirm-recipients-count').text() == "2 contacts selected"
	}

	def "'Forward' button works even when all messages are unchecked"() {
		given:
			createInboxTestMessages()
		when: 
			go "message/inbox/show/${Fmessage.findBySrc('Alice').id}"
			at PageMessageInbox
			messagesSelectAll.click()
		then:
			waitFor { checkedMessageCount == 2 }
		when:
			messagesSelectAll.click()
		then: 
			waitFor { $('#message-detail #message-detail-sender').text() == "Alice" }
		when:
			$('a', text:'Alice').click()
			$('#btn_forward').click()
		then:
			waitFor { $('div#tabs-1').displayed }
		then:
			$('textArea', name:'messageText').text() == "hi Alice"
	}
	
	def "should uncheck message when a different message is clicked"() {
		given:
			createInboxTestMessages()
		when: 
			to PageMessageInbox
			messagesSelect[1].click()
		then:
			messagesSelect[1].checked
		when:
			messagesSelect[1].click()
		then: 
			!messagesSelect[1].checked
	}


	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			Fmessage.build()
		when:
			to PageMessageInbox
			messagesSelect[0].click()
		then:
			waitFor { $('#multiple-messages').displayed }
			checkedMessageCount == 3
		when:
			messagesSelect[1].click()
		then:
			waitFor { checkedMessageCount == 2 }
		when:
			messagesSelect[0].click()
		then:
			waitFor { checkedMessageCount == 3 }
	}

	def "can archive multiple messages"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
		then:
			waitFor { at PageMessageInbox }
		when:
			messagesSelect[0].click()
		then:
			waitFor { deleteAllButton.displayed }
		when:
			assert $('#multiple-messages #btn_archive_all').displayed			
			$('#multiple-messages #btn_archive_all').jquery.trigger('click')
		then:
			waitFor { at PageMessageInbox }
			$("#message-detail-content").text() == 'No message selected'

	}
}
