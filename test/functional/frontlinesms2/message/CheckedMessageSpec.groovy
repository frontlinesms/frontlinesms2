package frontlinesms2.message

import frontlinesms2.*

class CheckedMessageSpec extends MessageGebSpec {
	
	def "header checkbox is checked when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			messagesSelect[1].click()
			messagesSelect[2].click()
			messagesSelect[3].click()
		then:
			waitFor { messagesSelect[0].@checked == "true" }
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $("#checked-message-count").text() == "2 messages selected" }
	}
	
	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.list()[0].id}"
			$(".message-select")[2].click()
		then:
			waitFor { $("#message-details #contact-name").text() == $(".displayName-${Fmessage.findBySrc('Bob').id}").text() }
		when:
			$(".message-select")[1].click()
		then:
			waitFor { $(".message-select")[1].parent().parent().hasClass("selected") }
			$(".message-select")[2].parent().parent().hasClass("selected")
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			new Contact(name: 'Alice', primaryMobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			to MessagesPage
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { $('#multiple-messages a')[0].displayed }
		when:
			$('#multiple-messages a')[0].click()
			$("div#tabs-1 .next").click()
		then:
			waitFor { $('input', value:'Alice').@checked }
			$('input', value:'Bob').@'checked'
			!$('input', value:'June').@'checked'
	}
	
	def "Should show the correct contact count when replying to multiple checked messages"() {
		given:
			[new Fmessage(src:'Alice', text:'hi Alice'),
				new Fmessage(src:'Alice', text:'test')].each() {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true)
				}
			new Contact(name: 'Alice', primaryMobile: 'Alice').save(failOnError:true)
		when:
			to MessagesPage
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
			new Contact(name: 'Alice', primaryMobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			to MessagesPage
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

	def "'Forward' button still works when all messages are unchecked"() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when: 
			to MessagesPage
			messagesSelect[0].click()
		then:
			waitFor { messagesSelect*.@checked == ["true", "true", "true"] }
		when:
			messagesSelect[0].click()
		then: 
			waitFor { messagesSelect*.@checked == [null, null, null] }
		when:
			$('#btn_dropdown').click()
		then:
			waitFor { $('#btn_forward').displayed }
		when:
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
			to MessagesPage
			messagesSelect[1].click()
		then:
			messagesSelect[1].@checked == "true"
		when:
			$('#messages tr:last-child td:nth-child(3) a').click()
		then: 
			!messagesSelect[1].@checked
	}


	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			new Fmessage(src: "src", dst: "dst", status: MessageStatus.INBOUND).save(flush: true)
		when:
			to MessagesPage
			messagesSelect[0].click()
		then:
			waitFor { $('#multiple-messages').displayed }
			$("#checked-message-count").text() == "3 messages selected"
		when:
			messagesSelect[1].click()
		then:
			waitFor { $("#checked-message-count").text() == "2 messages selected" }
		when:
			messagesSelect[0].click()
		then:
			waitFor { $("#checked-message-count").text() == "3 messages selected" }
	}

	def "can archive multiple messages"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
		then:
			waitFor {title == "Inbox"}
		when:
			$(".message-select")[0].click()
		then:
			waitFor { $('#multiple-messages #btn_archive_all').displayed }
		when:
			$('#multiple-messages #btn_archive_all').click()
		then:
			waitFor { at MessagesPage }
			$("div#no-messages").text() == 'No messages'

	}
}
