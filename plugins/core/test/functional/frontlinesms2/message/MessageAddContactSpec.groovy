package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.contact.*

@Mixin(frontlinesms2.utils.GebUtil)
class MessageAddContactSpec extends MessageBaseSpec {

	def 'if source of message does not exists in the database then number is displayed'() {
		setup:
			Fmessage.build(src:'+254778899', text:'test')
			def contactlessMessage = Fmessage.findBySrc('+254778899')
		when:
			to PageMessageInbox, contactlessMessage.id
		then:
			waitFor { messageList.messages[0].source == '+254778899' }
	}
	
	def 'add contact button is displayed and redirects to create contacts page with number field prepopulated'() {
		setup:
			Fmessage.build(src:'+254778899', text:'test')
			def message = Fmessage.findBySrc('+254778899')
		when:
			to PageMessageInbox, message.id
			singleMessageDetails.senderLink.click()
		then:
			waitFor { title == 'Contacts' }
			at PageContactShow
			singleContactDetails.mobile.value() == "+254778899"
	}
}
