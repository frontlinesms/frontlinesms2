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
			def messageSources = $('#messages tbody tr td:nth-child(2)')*.text()
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
			rowContents[1] == 'Alice'
			rowContents[2] == 'hi Alice'
			rowContents[3] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
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
			$('#messages .selected td:nth-child(2) a').getAttribute('href') == "/frontlinesms2/message/inbox/show/${aliceMessage.id}"
		when:
			go "message/inbox/show/${bobMessage.id}"
		then:
			$('#messages .selected td:nth-child(2) a').getAttribute('href') == "/frontlinesms2/message/inbox/show/${bobMessage.id}"
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
			def contact = new Contact(name: 'June', address: '+254778899').save(failOnError:true)
		when:
			to MessagesPage
			def rowContents = $('#messages tbody tr td:nth-child(2)')*.text()
		then:
			rowContents == ['June']
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
