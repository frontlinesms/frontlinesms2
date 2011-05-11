package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*

class InboxSpec extends grails.plugin.geb.GebSpec {
	def 'inbox message list is displayed'() {
		given:
			createTestMessages()
		when:
			to MessagesPage
			def messageSources = $('#messages tbody tr td:first-child')*.text()
		then:
			messageSources == ['Alice', 'Bob']
		cleanup:
			deleteTestMessages()
	}
    
	def 'message details are shown in list'() {
		given:
			createTestMessages()
		when:
			to MessagesPage
			def rowContents = $('#messages tbody tr:first-child td')*.text()
		then:
			rowContents[0] == 'Alice'
			rowContents[1] == 'hi Alice'
			rowContents[2] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
		cleanup:
			deleteTestMessages()
	}
    
	def 'message to alice is first in the list, and links to the show page'() {
		given:
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			to MessagesPage
			def firstMessageLink = $('#messages tbody tr:first-child a', href:"/frontlinesms2/message/inbox/${message.id}")
		then:
			firstMessageLink.text() == 'Alice'
		cleanup:
			deleteTestMessages()
	}
        
	def 'selected message and its details are displayed'() {
		given:
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			go "message/inbox/${message.id}"
			def formatedDate = dateToString(message.dateCreated)
		then:
			$('#message-details p:nth-child(1)').text() == message.src
			$('#message-details p:nth-child(2)').text() == formatedDate
			$('#message-details p:nth-child(3)').text() == message.text
		cleanup:
			deleteTestMessages()
	}
    
	def 'selected message is highlighted'() {
		given:
			createTestMessages()
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			go "message/inbox/${aliceMessage.id}"
		then:
			$('#messages .selected a').getAttribute('href') == "/frontlinesms2/message/inbox/${aliceMessage.id}"
		when:
			go "message/inbox/${bobMessage.id}"
		then:
			$('#messages .selected a').getAttribute('href') == "/frontlinesms2/message/inbox/${bobMessage.id}"
		cleanup:
			deleteTestMessages()
	}

	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice')].each() {
			it.inbound = true
			it.save(failOnError:true)
		}
	}

	static deleteTestMessages() {
		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

	String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat();
		return formatedDate.format(date)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("dd-MMM-yyyy hh:mm")
	}
}
