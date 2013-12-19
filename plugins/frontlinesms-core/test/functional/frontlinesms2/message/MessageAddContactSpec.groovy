package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.contact.*

@Mixin(frontlinesms2.utils.GebUtil)
class MessageAddContactSpec extends MessageBaseSpec {
	def 'if source of message does not exists in the database then number is displayed'() {
		setup:
			def contactlessMessageId = remote { TextMessage.build(src:'+254778899', text:'test').id }
		when:
			to PageMessageInbox, contactlessMessageId
		then:
			waitFor { messageList.messageSource(0) == '+254778899' }
	}
	
	def 'add contact button is displayed and redirects to create contacts page with number field prepopulated'() {
		setup:
			def messageId = remote { TextMessage.build(src:'+254778899', text:'test').id }
		when:
			to PageMessageInbox, messageId
			singleMessageDetails.senderLink.click()
		then:
			waitFor { title == 'contact.header' }
			at PageContactShow
			singleContactDetails.mobile.value() == "+254778899"
	}
}

