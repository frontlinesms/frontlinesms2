package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import frontlinesms2.popup.*
import frontlinesms2.*

import static frontlinesms.grails.test.EchoMessageSource.formatDate

class MessageInboxSpec extends MessageBaseSpec {
	def 'inbox message list is displayed'() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
		then:
			messageList.messageSource(0) == 'Alice'
			messageList.messageSource(1) == 'Bob'
	}

	def 'message details are shown in row'() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
		then:
			messageList.messageSource(1) == 'Bob'
			messageList.messageText(1) == 'hi Bob'
			messageList.messageDate(1) // ie is a valid date object
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
			def message = remote {
				def m = TextMessage.findBySrc('Alice')
				[id:m.id, src:m.src, text:m.text, date:m.date]
			}
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
			def aliceMessageId = remote { TextMessage.findBySrc('Alice').id }
			def bobMessageId = remote { TextMessage.findBySrc('Bob').id }
		when:
			to PageMessageInbox, aliceMessageId
		then:
			messageList.selectedMessageLinkUrl == "/message/inbox/show/${aliceMessageId}"
		when:
			to PageMessageInbox, bobMessageId
		then:
			messageList.selectedMessageLinkUrl == "/message/inbox/show/${bobMessageId}"
	}

	def 'CSS classes READ and UNREAD are set on corresponding messages'() {
		given:
			def mId = remote {
				TextMessage.build(read:false)
				TextMessage.build(read:true).id
			}
		when:
			to PageMessageInbox, mId
		then:
			messageList.isRead(0)
			!messageList.isRead(1)
	}

	def 'contact name is displayed if message src is an existing contact'() {
		given:
			remote {
				TextMessage.build(src:'+254778899')
				Contact.build(name:'June', mobile:'+254778899')
				null
			}
		when:
			to PageMessageInbox
		then:
			messageList.messageSource(0) == 'June'
	}

	def "should autopopulate the recipients name on click of reply even if the recipient is not in contact list"() {
		given:
			def message = remote {
				TextMessage.build(src:'+254778899', text:'test')
				Contact.build(name:'June', mobile:'+254778899')
				TextMessage.build(src:'+254999999').id
			}
		when:
			to PageMessageInbox, message
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
			to PageMessageInbox, remote { TextMessage.list()[0].id }
		then:
			messageList.messageCount() == 2
		when:
			footer.showStarred.click()
			waitFor { messageList.messageCount() == 1 }
		then:
			messageList.messageSource(0) == 'Alice'
		when:
			footer.showAll.click()
			waitFor { messageList.messageCount() == 2 }
		then:
			messageList.messageSource(0) == 'Alice'
			messageList.messageSource(1) == 'Bob'
	}

	def "should autopopulate the message body  when 'forward' is clicked"() {
		given:
			def message = remote {
				TextMessage.build(src:'+254778899', text:'test')
				TextMessage.build(src:'+254999999', text:'test').id
			}
		when:
			to PageMessageInbox, message
			waitFor { singleMessageDetails.forward.displayed }
			singleMessageDetails.forward.click()
			waitFor { at QuickMessageDialog }
		then:
			waitFor { textArea.text() == "test" }
	}

	def "message details should show the name of the route the message was received through"() {
		given:
			def messageId = remote {
				def con = SmslibFconnection.build(name:'MTN Dongle', port:'stormyPort')
				def message = TextMessage.build(src:'+254778899', text:'test')
				message.connectionId = con.id
				message.save(failOnError:true, flush:true)
				message.id
			}
		when:
			to PageMessageInbox, messageId
			waitFor { singleMessageDetails.receivedOn.displayed }
		then:
			singleMessageDetails.receivedOn.text() == 'fmessage.connection.receivedon[MTN Dongle]'
	}

	def "message details should not show the name of the route if none can be found"() {
		given:
			def message = remote { TextMessage.build(src:'+254778899', text:'test').id }
		when:
			to PageMessageInbox, message
		then:
			!singleMessageDetails.receivedOn.displayed
	}

	def "should only display message details when one message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
		then:
			waitFor {
				messageList.getCheckbox(0).disabled
			}
			waitFor {
				!messageList.getCheckbox(0).disabled
			}
		when:
			messageList.toggleSelect(1)
		then:
			waitFor('very slow') { multipleMessageDetails.displayed }
			waitFor('very slow') { multipleMessageDetails.checkedMessageCount == 2 }
		when:
			messageList.toggleSelect(1)
		then:
			waitFor { !multipleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "hi Alice" }
	}

	def "should show the address of the contact in the footer section"() {
		given:
			def message = remote {
				def m = new TextMessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true, flush:true)
				[id:m.id, src:m.src]
			}
		when:
			to PageMessageInbox, message.id
		then:
			singleMessageDetails.reply.click()
			waitFor { at QuickMessageDialog }
			recipientName() == message.src
	}
	
	def "should show the name of the contact in the confirm screen if contact exists"() {
		given:
			def message = remote {
				new Contact(name: "Tom", mobile: "+254999999").save(failOnError:true, flush:true)
				def m = new TextMessage(src:'+254999999', dst:'+254112233', text:'test', inbound:true).save(failOnError:true, flush:true)
				[id:m.id, srcName:Contact.findByMobile(m.src).name]
			}
		when:
			to PageMessageInbox, message.id
		then:
			singleMessageDetails.reply.click()
			waitFor { at QuickMessageDialog }
			recipientName() == message.srcName
	}

	def "should remain in the same page, after moving the message to the destination folder"() {
		setup:
			remote {
				new TextMessage(src: '1234567', date: new Date(), text: "hello", inbound:true).save(failOnError:true, flush:true)
				new Folder(name: "my-folder").save(failOnError:true, flush:true)
				null
			}
		when:
			to PageMessageInbox, remote { TextMessage.findByText('hello').id }
			singleMessageDetails.moveTo(remote { Folder.findByName('my-folder').id })
		then:
			waitFor("veryslow") { messageList.noContent.text() == 'fmessage.messages.none' }
			bodyMenu.selected == 'fmessage.section.inbox'
	}

	def "should update message count on tab when new message is received"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox, remote { TextMessage.findBySrc('Alice').id }
		then:
			tabs.unreadcount == 3
		when:
			remote { TextMessage.build().save(flush:true, failOnError:true); null }
		then:
			waitFor { tabs.unreadcount == 4 }
	}

	def "should show create contact link for a recipient that is not in the contact list"() {
		given: 'test message is created'
			remote { TextMessage.build(src:'369258147', text:'A sent message'); null }
		when : 'test message is selected'
			to PageMessageSent, remote { TextMessage.findBySrc('369258147').id }
		then : 'add contact icon is displayed'
			waitFor { singleMessageDetails.text == 'A sent message' }
			singleMessageDetails.addToContacts.displayed
	}

	def "should not show create contact link for a recipient that is in the contact list"() {
		given: 'test message is created'
			remote { TextMessage.build(src:'Donald', text:'A sent message'); null }
		when : 'test message is selected'
			to PageMessageSent, remote { TextMessage.findBySrc('Donald').id }
		then : 'add contact icon is displayed'
			waitFor { singleMessageDetails.text == 'A sent message' }
			!singleMessageDetails.addToContacts.displayed
	}

	def "should not show create contact link for multiple recipients that are not in the contact list"() {
		given: 'test message is created'
			def message = remote { new TextMessage(src:'000', inbound:false, text:"outgoing message to Pedro")
					.addToDispatches(dst:"+111", status:DispatchStatus.SENT, dateSent:new Date())
					.addToDispatches(dst:"+222", status:DispatchStatus.SENT, dateSent:new Date())
					.addToDispatches(dst:"+333", status:DispatchStatus.SENT, dateSent:new Date())
					.save(failOnError:true, flush:true).id }
		when : 'test message is selected'
			to PageMessageSent, message
		then : 'add contact icon is displayed'
			waitFor { singleMessageDetails.text == 'outgoing message to Pedro' }
			!singleMessageDetails.addToContacts.displayed
	}

	def "messageCount is shown in left-hand menu next to inbox indicator and is updated asynchronously"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
		then:
			bodyMenu.inboxNewMessageCount == 2
		when:
			messageList.toggleSelect(0)
		then:
			waitFor('veryslow') {
				bodyMenu.inboxNewMessageCount == 1
			}
	}

	def "pending message count is shown in left-hand menu next to pending indicator"() {
		given:
			createPendingTestMessages()
		when:
			to PageMessageInbox
		then:
			bodyMenu.pendingMessageCount == 2
	}

	boolean compareDatesIgnoreSeconds(Date a, Date b) {
		formatDate(a) == formatDate(b)
	}
}

