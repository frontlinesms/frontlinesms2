package frontlinesms2.message

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
class MessageAddContactSpec extends MessageBaseSpec {

	def 'if source of message does not exists in the database then number is displayed'() {
		when:
			createTestMessages()
			def contactlessMessage = Fmessage.findBySrc('+254778899')
			go "message/inbox/show/${contactlessMessage.id}"
		then:
			!Contact.findByPrimaryMobile(contactlessMessage.src)
			getColumnAsArray($('#messages tr'), 2) == ['Bob', 'Alice', '+254778899']
	}
	
	def 'add contact button is displayed and redirects to create contacts page with number field prepopulated'() {
		when:
			createTestMessages()
			def message = Fmessage.findBySrc('+254778899')
			go "message/inbox/show/${message.id}"
			def btnAddContact = $('a.button')
			assert btnAddContact instanceof geb.navigator.NonEmptyNavigator
			btnAddContact.click()
		then:
			waitFor {$('#contact_details').displayed}
			$('#contact_details').primaryMobile == "+254778899"
	}
}
