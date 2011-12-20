package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*

class MessageInboxSpec extends MessageBaseSpec {
	def 'inbox message list is displayed'() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			def messageSources = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			messageSources == ['Alice', 'Bob']
	}

	def 'message details are shown in row'() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			def rowContents = $('#messages tbody tr:nth-child(2) td')*.text()
		then:
			rowContents[2] == 'Bob'
			rowContents[3] == 'hi Bob'
			rowContents[4] ==~ /[0-9]{2} [A-Z][a-z]{3,9}, [0-9]{4} [0-9]{2}:[0-9]{2} [A-Z]{2}/
	}

	def 'message to alice is first in the list, and links to the show page'() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			to PageMessageInbox
			def firstMessageLink = $('a', text:"Alice")
		then:
			firstMessageLink.text() == 'Alice'
	}

	def 'no message is selected when inbox is first loaded'() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
		then:
			$('#message-detail #message-detail-content').text() == "No message selected"
	}
	
	//FIXME this test fail when the local computer language is different than english. The Date return
	//in the test in English while the UI date is in the local context
	def 'selected message and its details are displayed'() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			go "message/inbox/show/${message.id}"
			def formatedDate = dateToString(message.dateReceived)
		then:
			$('#message-detail #message-detail-sender').text() == message.src
			$('#message-detail #message-detail-date').text() == formatedDate
			$('#message-detail #message-detail-content').text() == message.text
	}

	def 'selected message is highlighted'() {
		given:
			createInboxTestMessages()
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			go "message/inbox/show/${aliceMessage.id}"
		then:
			$('#messages .selected td:nth-child(3) a').@href == "/core/message/inbox/show/${aliceMessage.id}?viewingArchive="
		when:
			go "message/inbox/show/${bobMessage.id}"
		then:
			$('#messages .selected td:nth-child(3) a').@href == "/core/message/inbox/show/${bobMessage.id}?viewingArchive="
	}

	def 'CSS classes READ and UNREAD are set on corresponding messages'() {
		given:
			def m1 = new Fmessage(inbound:true, read: false).save(failOnError:true)
			def m2 = new Fmessage(inbound:true, read: true).save(failOnError:true)
			assert !m1.read
			assert m2.read
		when:
			go "message/inbox/show/$m2.id"
		then:
			$("tr#message-${m1.id}").hasClass('unread')
			!$("tr#message-${m1.id}").hasClass('read')

			!$("tr#message-${m2.id}").hasClass('unread')
			$("tr#message-${m2.id}").hasClass('read')
	}

	def 'contact name is displayed if message src is an existing contact'() {
		given:
			def message = new Fmessage(src:'+254778899', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
			def contact = new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			to PageMessageInbox
			def rowContents = $('#messages tbody tr td:nth-child(3)')*.text()
		then:
			rowContents == ['June']
	}

	def "should autopopulate the recipients name on click of reply even if the recipient is not in contact list"() {
		given:
			new Fmessage(src:'+254778899', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
		when:
			go "message/inbox/show/$message.id"
			$('#btn_reply').click()
		then:
			waitFor { $('div#tabs-1').displayed }
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
	}

	def "starred message filter should not be visible when there are no search results"() {
		when:
			go "message/inbox"
		then:
			$("#no-messages").text() == "No messages"
		    !$("a", text:"starred").displayed
	}

	def "should autopopulate the message body  when 'forward' is clicked"() {
		given:
			new Fmessage(src:'+254778899', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
		when:
			go "message/inbox/show/$message.id"
			waitFor{$("#btn_dropdown").displayed}
			$("#btn_dropdown").click()
			waitFor{$("#btn_forward").displayed}
			$("#btn_forward").click()
			waitFor {$('div#tabs-1').displayed}
		then:
			$('textArea', name:'messageText').text() == "test"
	}
	
	def "should only display message details when one message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { checkedMessageCount == 2 }
		when:
			messagesSelect[2].click()
			def message = Fmessage.findBySrc('Alice')
			def formatedDate = dateToString(message.dateReceived)
		then:
			waitFor { checkedMessageCount == 1 }
			$('#message-detail #message-detail-sender').text() == message.src
			$('#message-detail #message-detail-date').text() == formatedDate
			$('#message-detail #message-detail-content').text() == message.text
	}

	def "should skip recipients tab if a message is replied"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
		then:
			$("#btn_reply").click()
			waitFor {$('#tabs-1').displayed}
		when:
			$("#nextPage").jquery.trigger('click')
			waitFor { $('#tabs-3 ').displayed }
		then:
			$("#tabs-3").displayed
	}
	
	def "should show the address of the contact in the confirm screen"() {
		given:
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
			
		when:
			go "message/inbox/show/${message.id}"
		then:
			$("#btn_reply").click()
			waitFor {$('#tabs-1').displayed}
		when:
			$("#nextPage").jquery.trigger('click')
			waitFor { $('#tabs-3 ').displayed }
		then:
			$("#tabs-3").displayed
			$("#recipient").text() == "${message.src}"
	}
	
	def "should show the name of the contact in the confirm screen if contact exists"() {
		given:
			new Contact(name: "Tom", primaryMobile: "+254999999").save(failOnError:true)
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
			
		when:
			go "message/inbox/show/${message.id}"
		then:
			$("#btn_reply").click()
			waitFor {$('#tabs-1').displayed}
		when:
			$("#nextPage").jquery.trigger('click')
			waitFor { $('#tabs-3 ').displayed }
		then:
			$("#tabs-3").displayed
			$("#recipient").text() == "${Contact.findByPrimaryMobile(message.src).name}"
	}

	//FIXME
//	def "should skip recipients tab for reply-all option"() {
//		given:
//			createInboxTestMessages()
//		when:
//			go "message"
//			$("#message")[0].click()
//			sleep 1000
//			$("#reply-all").click()
//			sleep 10000
//		then:
//			$("#tabs").find { $("a").@href == '#tabs1' }
//			!$("#tabs").find { $("a").@href == '#tabs2' }
//			$("#tabs").find { $("a").@href == '#tabs3' }
//			!$("#tabs a").@href('#tabs2').displayed
//			$("#tabs a").@href('#tabs3').displayed
//	}
	
	def "should remain in the same page, after moving the message to the destination folder"() {
		setup:
			new Fmessage(text: "hello", inbound:true).save(failOnError:true)
			new Folder(name: "my-folder").save(failOnError:true, flush:true)
		when:
			go "message/inbox/show/${Fmessage.findByText('hello').id}"
			$("#move-actions").jquery.val(Folder.findByName('my-folder').id.toString()) // TODO please note why we are using jquery here - if it's necessary, that is
			$("#move-actions").jquery.trigger("change")
		then:	
			waitFor { $("#no-messages").displayed && $("#no-messages").text().contains("No messages") }
			$("#messages-submenu .selected").text().contains('Inbox')
	}
	
	def "should update message count when new message is received"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.findBySrc('Alice').id}"
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text: "message count", inbound:true).save(flush: true, failOnError:true)
		then:
			$("#message-tab-link").text() == "Messages\n1"
		when:
			js.refreshMessageCount()
		then:
			waitFor { $("#message-tab-link").text() == "Messages\n2" }
	}

	String dateToString(Date date) {
		new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US).format(date)
	}
}
