package frontlinesms2.message

import frontlinesms2.popup.*
import frontlinesms2.*

class MessageViewSpec extends grails.plugin.geb.GebSpec {
	def "message with special html characters should display in list"(){
		given: 'a test message has been created'
			remote { TextMessage.build(src:'Bob', text:'<hello>'); null }
		when: 'Inbox is open'
			to PageMessageInbox
		then: 'message column should display the appropriate text'
			messageList.messageSource(0) == 'Bob'
			messageList.messageText(0) == '<hello>'
	}
}

