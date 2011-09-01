package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class CheckedMessageSpec extends MessageGebSpec {
	
	def "header checkbox is checked when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			$("#message")[1].click()
			$("#message")[2].click()
			$("#message")[3].click()
		then:
			$("#message")[0].@checked == "true"
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			$("#message")[1].click()
			$("#message")[2].click()
			sleep 1000
		then:
			$("#checked-message-count").text() == "2 messages selected"
	}
	
	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.list()[0].id}"
			$("#message")[1].click()
			$("#message")[2].click()
		then:
			$("tr#message-${Fmessage.list()[0].id}").hasClass('selected')
			$("tr#message-${Fmessage.list()[1].id}").hasClass('selected')
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			new Contact(name: 'Alice', primaryMobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			go "message/inbox"
			$("#message")[1].click()
			$("#message")[2].click()
			sleep 1000
			def btnReply = $('#multiple-messages a')[0]
		then:
			btnReply
		when:
			btnReply.click()
			sleep 1000	
			$("div#tabs-1 .next").click()
		then:
			$('input', value:'Alice').getAttribute('checked')
			$('input', value:'Bob').getAttribute('checked')
			!$('input', value:'June').getAttribute('checked')
	}
	
//	FIXME
//	def "'Forward' button still work when all messages are unchecked"() {
//		given:
//			createInboxTestMessages()
//			def message = Fmessage.findBySrc('Alice')
//		when: 
//			to MessagesPage
//			$("#message")[0].click()
//		then:
//			$("#message")*.@checked == ["true", "true", "true"]
//		when:
//			$("#message")[0].click()
//		then: 
//			$("#message")*.@checked == ["", "", ""]
//		when:
//			$('#btn_dropdown').click()
//			$('#btn_forward').click()			
//			waitFor {$('div#tabs-1').displayed}
//		then:
//			$('textArea', name:'messageText').text() == "hi Alice"
//	}
	
	def "should uncheck message when a different message is clicked"() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Bob')
		when: 
			to MessagesPage
			$("#message")[1].click()
		then:
			$("#message")[1].@checked == "true";
		when:
			$('#messages tr:last-child td:nth-child(3) a').click()
		then: 
			$("#message")[1].@checked == ""
	}


	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			new Fmessage(src: "src", dst: "dst", status: MessageStatus.INBOUND).save(flush: true)
		when:
			to MessagesPage
			$("#message")[0].click()
			sleep(1000)
			waitFor { $('#multiple-messages').displayed}
		then:
			$("#checked-message-count").text() == "3 messages selected"
		when:
			$('#message')[1].click()
			sleep(1000)
			waitFor { $("#checked-message-count").text().contains("2") }
		then:
			$("#checked-message-count").text() == "2 messages selected"
		when:
			$('#message')[0].click()
			sleep(1000)
			waitFor { $("#checked-message-count").text().contains("3") }
		then:
			$("#checked-message-count").text() == "3 messages selected"
	}

	def "can archive multiple messages"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
			waitFor {title == "Inbox"}
			$("#message")[0].click()
			sleep 1000
			def btnArchive = $('#multiple-messages #btn_archive_all')
			btnArchive.click()
			sleep 1000
		then:
			at MessagesPage
			$("div#no-messages").text() == 'No messages'

	}
}
