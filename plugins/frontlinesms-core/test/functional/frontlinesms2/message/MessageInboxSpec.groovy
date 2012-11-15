package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import frontlinesms2.popup.*
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
			messageList.messages[1].source == 'Bob'
			messageList.messages[1].text == 'hi Bob'
			messageList.messages[1].date != null // ie is a valid date object
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
			to PageMessageInbox, message.id
		then:
			waitFor { singleMessageDetails.displayed }
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
			to PageMessageInbox, aliceMessage.id
		then:
			messageList.selectedMessages[0].linkUrl == "/message/inbox/show/${aliceMessage.id}"
		when:
			to PageMessageInbox, bobMessage.id
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
			to PageMessageInbox, m2.id
		then:
			messageList.messages[0].isRead
			!messageList.messages[1].isRead
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
			to PageMessageInbox, message.id
			singleMessageDetails.reply.click()
		then:
			//FIXME: does this test really check what the title suggests?
			// FIXME no it doesn't!  Not entirely clear what it is trying to test to me.
			waitFor { at QuickMessageDialog }
	}

	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
			createInboxTestMessages()
		when:
			to PageMessageInbox, Fmessage.list()[0].id
		then:
			messageList.messages.size() == 2
		when:
			footer.showStarred.click()
			waitFor { messageList.messages.size() == 1 }
		then:
			messageList.messages[0].source == 'Alice'
		when:
			footer.showAll.click()
			waitFor { messageList.messages.size() == 2 }
		then:
			messageList.sources.containsAll(['Alice', 'Bob'])
	}

	def "should autopopulate the message body  when 'forward' is clicked"() {
		given:
			Fmessage.build(src:'+254778899', text:'test')
			def message = Fmessage.build(src:'+254999999', text:'test')
		when:
			to PageMessageInbox, message.id
			waitFor{ singleMessageDetails.forward.displayed }
			singleMessageDetails.forward.click()
			waitFor { at QuickMessageDialog }
		then:
			waitFor { compose.textArea.text() == "test" }
	}

	def "message details should show the name of the route the message was received through"() {
		given:
			def con = SmslibFconnection.build(name:'MTN Dongle', port:'stormyPort')
			def message = Fmessage.build(src:'+254778899', text:'test')
			con.addToMessages(message)
			con.save(flush:true)
		when:
			to PageMessageInbox, message.id
			waitFor{ singleMessageDetails.receivedOn.displayed }
		then:
			singleMessageDetails.receivedOn.text() == "Received on: MTN Dongle"
	}

	def "message details should not show the name of the route if none can be found"() {
		given:
			def message = Fmessage.build(src:'+254778899', text:'test')
		when:
			to PageMessageInbox, message.id
		then:
			!singleMessageDetails.receivedOn.displayed
	}

	def "should only display message details when one message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.messages[0].checkbox.click()
			messageList.messages[1].checkbox.click()
		then:
			waitFor("veryslow") { multipleMessageDetails.displayed }
			waitFor("veryslow") { multipleMessageDetails.checkedMessageCount == "2 messages selected" }
		when:
			messageList.messages[1].checkbox.click()
		then:
			waitFor { !multipleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "hi Alice" }
	}

	def "should skip recipients tab if a message is replied"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox, Fmessage.findBySrc('Bob').id
		then:
			singleMessageDetails.reply.click()
			waitFor { at QuickMessageDialog }
		when:
			next.jquery.trigger('click')
		then:
			waitFor { confirm.displayed }
	}
	
	def "should show the address of the contact in the confirm screen"() {
		given:
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true, flush:true)
			
		when:
			to PageMessageInbox, message.id
		then:
			singleMessageDetails.reply.click()
			waitFor { at QuickMessageDialog }
		when:
			next.jquery.trigger('click')
			waitFor { confirm.displayed }
		then:
			confirm.recipientName == message.src
	}
	
	def "should show the name of the contact in the confirm screen if contact exists"() {
		given:
			new Contact(name: "Tom", mobile: "+254999999").save(failOnError:true, flush:true)
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true, flush:true)
			
		when:
			to PageMessageInbox, message.id
		then:
			singleMessageDetails.reply.click()
			waitFor { at QuickMessageDialog }
		when:
			next.jquery.trigger('click')
			waitFor { confirm.displayed }
		then:
			confirm.recipientName == "${Contact.findByMobile(message.src).name}"
	}

	def "should skip recipients tab for reply-all option"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.selectAll.click()
			waitFor { multipleMessageDetails.replyAll.displayed }
			multipleMessageDetails.replyAll.click()
		then:
			waitFor { at QuickMessageDialog }
			compose.displayed
		when:
			next.click()
		then:
			waitFor { confirm.displayed }
	}

	def "should remain in the same page, after moving the message to the destination folder"() {
		setup:
			new Fmessage(src: '1234567', date: new Date(), text: "hello", inbound:true).save(failOnError:true, flush:true)
			new Folder(name: "my-folder").save(failOnError:true, flush:true)
		when:
			to PageMessageInbox, Fmessage.findByText('hello').id
			singleMessageDetails.moveTo(Folder.findByName('my-folder').id)
		then:
			waitFor("veryslow") { messageList.noContent.text() == "No messages here!" }
			bodyMenu.selected == "inbox"
	}

	def "should update message count on tab when new message is received"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox, Fmessage.findBySrc('Alice').id
		then:
			tabs.unreadcount == 1
		when:
			Fmessage.build().save(flush:true, failOnError:true)
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
