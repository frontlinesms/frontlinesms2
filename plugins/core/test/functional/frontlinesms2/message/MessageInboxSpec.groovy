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
		then:
			messageList.sources.containsAll(['Alice', 'Bob'])
	}

	def 'message details are shown in row'() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
		then:
			messageList.messages[2].source == 'Bob'
			messageList.messages[2].text == 'hi Bob'
			messageList.messages[2].date != null // ie is a valid date object
	}

	def 'message to alice is first in the list, and links to the show page'() {
		// TODO: rewrite (but first understand what it's doing.. seems strange)
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
			singleMessageDetails.displayed && singleMessageDetails.noneSelected
	}
	
	//FIXME this test fail when the local computer language is different than english. The Date return
	//in the test in English while the UI date is in the local context
	def 'selected message and its details are displayed'() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			to PageMessageInbox, "show", message.id
		then:
			singleMessageDetails.sender == message.src
			compareDatesIgnoreSeconds(singleMessageDetails.date, message.date)
			singleMessageDetails.text == message.text
	}

	def 'selected message is highlighted'() {
		given:
			createInboxTestMessages()
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			to PageMessageInbox, "show", aliceMessage.id
		then:
			messageList.selectedMessages[0].linkUrl == "/message/inbox/show/${aliceMessage.id}"
		when:
			to PageMessageInbox, "show", bobMessage.id
		then:
			messageList.selectedMessages[0].linkUrl == "/message/inbox/show/${bobMessage.id}"
	}

	def 'CSS classes READ and UNREAD are set on corresponding messages'() {
		given:
			def m1 = Fmessage.build(read:false)
			def m2 = Fmessage.build(read:true)
			assert !m1.read
			assert m2.read
		when:
			to PageMessageInbox, "show", m2.id
		then:
			!messageList.messages[0].isRead
			messageList.messages[1].isRead
	}

	def 'contact name is displayed if message src is an existing contact'() {
		given:
			Fmessage.build(src:'+254778899')
			Contact.build(name:'June', mobile:'+254778899')
		when:
			to PageMessageInbox
		then:
			messageList.sources.contains('June')
	}

	def "should autopopulate the recipients name on click of reply even if the recipient is not in contact list"() {
		given:
			Fmessage.build(src:'+254778899', text:'test')
			Contact.build(name:'June', mobile:'+254778899')
			def message = Fmessage.build(src:'+254999999')
		when:
			to PageMessageInbox, 'show', message.id
			singleMessageDetails.reply.click()
		then:
			//FIXME: does this test really check what the title suggests?
			// FIXME no it doesn't!  Not entirely clear what it is trying to test to me.
			waitFor { quickMessageDialog.compose.textArea.displayed }
	}

	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
			createInboxTestMessages()
		when:
			to PageMessageInbox, "show", Fmessage.list()[0].id
		then:
			messageList.messages.size() == 3
		when:
			footer.showStarred.click()
			waitFor { messageList.messages.size() == 2 }
		then:
			messageList.messages[1].source == 'Alice'
		when:
			footer.showAll.click()
			waitFor { messageList.messages.size() == 3 }
		then:
			messageList.sources.containsAll(['Alice', 'Bob'])
	}

	def "starred message filter should not be visible when there are no search results"() {
		when:
			to PageMessageInbox
		then:
			messageList.noContent.displayed
			!footer.showStarred.displayed
	}

	def "should autopopulate the message body  when 'forward' is clicked"() {
		given:
			Fmessage.build(src:'+254778899', text:'test')
			def message = Fmessage.build(src:'+254999999', text:'test')
		when:
			to PageMessageInbox, "show", message.id
			waitFor{ singleMessageDetails.forward.displayed }
			singleMessageDetails.forward.click()
			waitFor { quickMessageDialog.compose.textArea.displayed }
		then:
			quickMessageDialog.compose.textArea.text() == "test"
	}
	
	def "should only display message details when one message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.messages[1].checkbox.click()
			messageList.messages[2].checkbox.click()
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == 2 }
		when:
			messageList.messages[2].checkbox.click()
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == 1 }
			waitFor { singleMessageDetails.displayed }
	}

	def "should skip recipients tab if a message is replied"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox, "show", Fmessage.findBySrc('Bob').id
		then:
			singleMessageDetails.reply.click()
			waitFor { quickMessageDialog.compose.textArea.displayed }
		when:
			quickMessageDialog.next.jquery.trigger('click')
		then:
			waitFor { quickMessageDialog.confirm.displayed }
	}
	
	def "should show the address of the contact in the confirm screen"() {
		given:
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
			
		when:
			to PageMessageInbox, "show", message.id
		then:
			singleMessageDetails.reply.click()
			waitFor { quickMessageDialog.compose.textArea.displayed }
		when:
			quickMessageDialog.next.jquery.trigger('click')
			waitFor { quickMessageDialog.confirm.displayed }
		then:
			quickMessageDialog.confirm.recipientName == message.src
	}
	
	def "should show the name of the contact in the confirm screen if contact exists"() {
		given:
			new Contact(name: "Tom", mobile: "+254999999").save(failOnError:true)
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true)
			
		when:
			to PageMessageInbox, "show", message.id
		then:
			singleMessageDetails.reply.click()
			waitFor { quickMessageDialog.compose.textArea.displayed }
		when:
			quickMessageDialog.next.jquery.trigger('click')
			waitFor { quickMessageDialog.confirm.displayed }
		then:
			quickMessageDialog.confirm.recipientName == "${Contact.findByMobile(message.src).name}"
	}

	// FIXME FOR THE BELOW FIXME.  IF YOU WILL INSIST ON COMMENTING OUT STUFF LIKE THIS, PLEASE EXPLAIN WHAT IS BROKEN
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
			new Fmessage(src: '1234567', date: new Date(), text: "hello", inbound:true).save(failOnError:true)
			new Folder(name: "my-folder").save(failOnError:true, flush:true)
		when:
			to PageMessageInbox, "show", Fmessage.findByText('hello').id
			singleMessageDetails.moveTo("my-folder")
		then:	
			waitFor { messageList.noContent.displayed }
			bodyMenu.selected == "inbox"
	}
	
	def "should update message count on tab when new message is received"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox, "show", Fmessage.findBySrc('Alice').id
		then:
			tabs.unreadcount == 1
		when:
			Fmessage.build().save(flush: true, failOnError:true)
			js.refreshMessageCount()
		then:
			waitFor { tabs.unreadcount == 2}
	}

	String dateToString(Date date) {
		new SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.US).format(date)
	}

	boolean compareDatesIgnoreSeconds(Date a, Date b) {
		dateToString(a) == dateToString(b)
	}
}
