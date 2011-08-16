package frontlinesms2.message

import frontlinesms2.*

class CheckedMessageSpec extends MessageGebSpec {
	
	def "should check the header checkbox when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			$("#message")[1].click()
			$("#message")[2].click()
		then:
			$("#message")[0].@checked == "true"
		cleanup:
			deleteTestMessages()
	}
	
	def "should display message count when multiple messages are selected"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			$("#message")[1].click()
			$("#message")[2].click()
		then:
			$("#checked-message-count").text() == "2 messages selected"
		cleanup:
			deleteTestMessages()
	}
	
	def "should remained checked when single message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			$("#message")[1].click()
			$("#message")[2].click()
		then:
			$("#checked-message-count").text() == "2 messages selected"
		when:
			$("#message")[1].click()
		then:
			$("#message")[2].@checked == "true"
		cleanup:
			deleteTestMessages()
	}
	
	def "should change CSS to CHECKED when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.list()[0].id}"
			$("#message")[1].click()
			$("#message")[2].click()
		then:
			$("tr#message-${Fmessage.list()[0].id}").hasClass('checked')
			$("tr#message-${Fmessage.list()[1].id}").hasClass('checked')
		cleanup:
			deleteTestMessages()
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
			waitFor {$('#multiple-message').displayed}
			def btnReply = $('#multiple-message a')[0]
		then:
			btnReply
		when:
			btnReply.click()
			waitFor {$('div#tabs-1').displayed}
			$("div#tabs-1 .next").click()
		then:
			$('input', value:'Alice').getAttribute('checked')
			$('input', value:'Bob').getAttribute('checked')
			!$('input', value:'June').getAttribute('checked')
			
		cleanup:
			deleteTestMessages()
			deleteTestContacts()
	}
	
	def "'Forward' button still work when all messages are unchecked"() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when: 
			to MessagesPage
			$("#message")[0].click()
		then:
			$("#message")*.@checked == ["true", "true", "true"]
		when:
			$("#message")[0].click()
		then: 
			$("#message")*.@checked == ["", "", ""]
		when:
			$('#btn_dropdown').click()
			$('#btn_forward').click()			
			waitFor {$('div#tabs-1').displayed}
		then:
			$('textArea', name:'messageText').text() == "hi Alice"
	}
	
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

	def "should archive multiple messages"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
			$("#message")[0].click()
			waitFor { $("#multiple-message").displayed }
			def btnArchive = $('#multiple-message #btn_archive_all')
			btnArchive.click()
			sleep 1000
			waitFor {$("div#no-messages").displayed}
		then:
			at MessagesPage
			$("div#no-messages").text() == 'No messages'

	}
}
