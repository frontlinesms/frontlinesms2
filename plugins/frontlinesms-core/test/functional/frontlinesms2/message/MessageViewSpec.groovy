package frontlinesms2.message

import frontlinesms2.popup.*
import frontlinesms2.*

class MessageViewSpec extends grails.plugin.geb.GebSpec {

	def "message with special html characters should display in list"(){
		given: 'a test message has been created'
			remote { Fmessage.build(src:'Bob', text:'<hello>'); null }
		when: 'Inbox is open'
			to PageMessageInbox
		then: 'message column should display the appropriate text'
			messageList.messageSource(0) == 'Bob'
			messageList.messageText(0) == '<hello>'
	}

	def "message with special html characters should display in confirm tab"(){
		when: 'Quick Message Dialog is launched'
			to PageMessageInbox
			header.quickMessage.click()
			waitFor { at QuickMessageDialog }
		and: 'test message is entered'
			compose.textArea.value('<hello>')
			next.click()
		and: 'test contact is entered'
			waitFor { recipients.displayed }
			recipients.addField = '+254123456'
			next.click()
		then: 'confirm details must be accurate'
			waitFor { confirm.displayed }
			confirm.messagesToSendCount == '1'	
			confirm.messageText == '<hello>'
	}
}
