package frontlinesms2.message

import frontlinesms2.*

class AddContactSpec extends MessageGebSpec {
	def 'if source of message does not exists in the database then number is displayed'() {
		when:
			createTestMessages()
			def contactlessMessage = Fmessage.findBySrc('+254778899')
			go "message/inbox/${contactlessMessage.id}"
		then:
			!Contact.findByAddress(contactlessMessage.src)
			$('#message-details p:nth-child(1)').text() == '+254778899'
		cleanup:
			deleteTestMessages()
			
	}
	def 'if source of message exists in the database then contact name is displayed'() {
		when:
			createTestContacts()
			createTestMessages()
			def message = Fmessage.findBySrc('+254778899')
			go "message/inbox/${message.id}"

		then:
			Contact.findByAddress(message.src)
			$('#message-details p:nth-child(1)').text() == 'Alice'
		cleanup:
			deleteTestMessages()
			deleteTestContacts()
	}
	
	def "add contact button is displayed and redirects to create contacts page with number field prepopulated"() {
		when:
			createTestMessages()
			def message = Fmessage.findBySrc('+254778899')
			go "message/inbox/${message.id}"
			def btnAddContact = $('a.button')
			assert btnAddContact instanceof geb.navigator.NonEmptyNavigator
			btnAddContact.click()
		then:
			$('#contact-details').address == "+254778899"
		cleanup:
			deleteTestMessages()
	}
}

