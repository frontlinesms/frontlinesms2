package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class InboxSpec extends MessageGebSpec {
	def 'inbox message list is displayed'() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			def messageSources = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			messageSources == ['Alice', 'Bob']
		cleanup:
			deleteTestMessages()
	}
    
	def 'message details are shown in list'() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[2] == 'Alice'
			rowContents[3] == 'hi Alice'
			rowContents[4] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
		cleanup:
			deleteTestMessages()
	}
    
	def 'message to alice is first in the list, and links to the show page'() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			to MessagesPage
			def firstMessageLink = $('#messages tbody tr:nth-child(1) a', href:"/frontlinesms2/message/inbox/show/${message.id}")
		then:
			firstMessageLink.text() == 'Alice'
		cleanup:
			deleteTestMessages()
	}
        
	def 'selected message and its details are displayed'() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			go "message/inbox/show/${message.id}"
			def formatedDate = dateToString(message.dateCreated)
		then:
			$('#message-details p:nth-child(1)').text() == message.src
			$('#message-details p:nth-child(3)').text() == formatedDate
			$('#message-details p:nth-child(4)').text() == message.text
		cleanup:
			deleteTestMessages()
	}
    
	def 'selected message is highlighted'() {
		given:
			createInboxTestMessages()
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			go "message/inbox/show/${aliceMessage.id}"
		then:
			$('#messages .selected td:nth-child(3) a').getAttribute('href') == "/frontlinesms2/message/inbox/show/${aliceMessage.id}"
		when:
			go "message/inbox/show/${bobMessage.id}"
		then:
			$('#messages .selected td:nth-child(3) a').getAttribute('href') == "/frontlinesms2/message/inbox/show/${bobMessage.id}"
		cleanup:
			deleteTestMessages()
	}
	
	def 'CSS classes READ and UNREAD are set on corresponding messages'() {
		given:
			def m1 = new Fmessage(status:MessageStatus.INBOUND, read: false).save(failOnError:true)
			def m2 = new Fmessage(status:MessageStatus.INBOUND, read: true).save(failOnError:true)
			assert !m1.read
			assert m2.read
		when:
			go "message/inbox/show/$m2.id"
		then:
			$("tr#message-${m1.id}").hasClass('unread')
			!$("tr#message-${m1.id}").hasClass('read')
			
			!$("tr#message-${m2.id}").hasClass('unread')
			$("tr#message-${m2.id}").hasClass('read')
		cleanup:
			deleteTestMessages()
	}
	
	def 'contact name is displayed if message src is an existing contact'() {
		given:
			def message = new Fmessage(src:'+254778899', dst:'+254112233', text:'test', status:MessageStatus.INBOUND).save(failOnError:true)
			def contact = new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			to MessagesPage
			def rowContents = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			rowContents == ['June']
		cleanup:
			deleteTestMessages()
			deleteTestContacts()
	}

	def "should autopopulate the recipients name on click of reply for a inbox message"() {
		given:
			new Fmessage(src:'+254778899', dst:'+254112233', text:'test', status:MessageStatus.INBOUND).save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			to MessagesPage
			println $('#btn_reply').text()
			$('#btn_reply').click()
			waitFor {$('div#tabs-1').displayed}
			$("div#tabs-1 .next").click()
		then:
			$('input', value:'+254778899').getAttribute('checked')
		cleanup:
			deleteTestMessages()
			deleteTestContacts()
	}

	def "should autopopulate the recipients name on click of reply even if the recipient is not in contact list"() {
		given:
			new Fmessage(src:'+254778899', dst:'+254112233', text:'test', status:MessageStatus.INBOUND).save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', status:MessageStatus.INBOUND).save(failOnError:true)
		when:
			go "message/inbox/show/${message.id}"	
			$('#btn_reply').click()
			waitFor {$('div#tabs-1').displayed}
			$("div#tabs-1 .next").click()
		then:
			$('input', value:'+254999999').getAttribute('checked')
			!$('input', value:'+254778899').getAttribute('checked')
		cleanup:
			deleteTestMessages()
			deleteTestContacts()
	}

	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
	    	createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.list()[0].id}"
		then:    	
			$("#messages tbody tr").size() == 2
		when:	
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'Alice'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['Alice', 'Bob'])
		cleanup:
			deleteTestMessages()
	}
	
	def "should autopopulate the message body  when 'forward' is clicked"() {
		given:
			new Fmessage(src:'+254778899', dst:'+254112233', text:'test', status:MessageStatus.INBOUND).save(failOnError:true)
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', status:MessageStatus.INBOUND).save(failOnError:true)
		when:
			go "message/inbox/show/${message.id}"
			$('#btn_dropdown').click()
			$('#btn_forward').click()			
			waitFor {$('div#tabs-1').displayed}
		then:
			$('textArea', name:'messageText').text() == "test"
		cleanup:
			deleteTestMessages()
	}
	
	def "should only display message details when one message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			$("#message")[1].click()
			$("#message")[2].click()
		then:
			$('#message-details p:nth-child(1)').text() == "2 messages selected"
		when:
			$("#message")[1].click()
			def message = Fmessage.findBySrc('Bob')
		then:
			$('#message-details p:nth-child(1)').text() == message.src
			$('#message-details p:nth-child(3)').text() == formatedDate
			$('#message-details p:nth-child(4)').text() == message.text
		
		cleanup:
			deleteTestMessages()
	}
	
	def "should check all the messages when the header checkbox is checked"() {
		given:
			createInboxTestMessages()
		when:
			to MessagesPage
			$("#message")[0].click()
		then:
			$("#message")*.@checked == ["true", "true", "true"]
		cleanup:
			deleteTestMessages()
	}
	
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
			$('#message-details p:nth-child(1)').text() == "2 messages selected"
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
			def aliceMessage = Fmessage.findBySrc('Alice')
			def message3 = Fmessage.findBySrc('+254778899')
		when:
			go "message/inbox"
			$("#message")[1].click()
			$("#message")[2].click()
			println ">>>"+$('#message-details div.buttons').text()
			waitFor {$('#message-details div.buttons').text().contains("Reply All")}
			def btnReply = $('#message-details div.buttons a')[0]
		then:
			btnReply
		when:
			btnReply.click()
			waitFor {$('div#tabs-1').displayed}
			$("div#tabs-1 .next").click()
		then:
			$('input', value:'+254999999').getAttribute('checked')
			!$('input', value:'+254778899').getAttribute('checked')
		cleanup:
			deleteTestMessages()
			deleteTestContacts()
	}

	String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat();
		return formatedDate.format(date)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("dd-MMM-yyyy hh:mm")
	}
}
