package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec
import frontlinesms2.message.*

class ContactEditSpec extends ContactBaseSpec {
	def setup() {
		createTestContacts()
	}
	
	def 'selected contact details can be edited and saved'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
		when:
			to PageContactShow, aliceId

			singleContactDetails.name.value('Kate')
			singleContactDetails.mobile.value('+2541234567')
			singleContactDetails.email.value('gaga@gmail.com')
			singleContactDetails.save.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('mobile', 'Mobile', '+2541234567')
			remote { Contact.findById(aliceId).name } == 'Kate'
	}

	def "Updating a contact within a group keeps the view inside the group"() {
		given:
			def groupId = remote {
				def alice = Contact.findByName('Alice')
				Group g = new Group(name: 'Excellent').save(failOnError:true, flush:true)
				alice.addToGroups(g)
				alice.save(flush:true)
				g.id
			}
		when:
			to PageGroupShow, groupId, remote { Contact.findByName('Alice').id }
			singleContactDetails.name.value('Kate')
			singleContactDetails.mobile.value('+2541234567') 
			singleContactDetails.email.value('gaga@gmail.com')
			singleContactDetails.save.click()
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			remote { Contact.findByName('Kate') != null }
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('mobile', 'Mobile', '+2541234567')
			bodyMenu.selectedMenuItem == 'excellent'
	}
	
	def "should remove address when delete icon is clicked"() {
		when:
			to PageContactShow, remote { Contact.findByName('Bob').id }
		then:
			singleContactDetails.removeMobile.displayed
		when:
			singleContactDetails.removeMobile.click()
		then:
			!singleContactDetails.removeMobile.displayed
			!singleContactDetails.sendMessage.displayed
	}
	
	def "should disable the save and cancel buttons when viewing a contact details"() {
		when:
			to PageContactShow, remote { Contact.findByName('Bob').id }
		then:
			singleContactDetails.save.disabled
	}
	
	def "should enable save and cancel buttons when contact details are edited"() {
		when:
			to PageContactShow, remote { Contact.findByName('Bob').id }
			singleContactDetails.email.value('bob@gmail.com')
		then:
			!singleContactDetails.save.disabled
			!singleContactDetails.cancel.disabled
	}
	
	def "should remain on the same page after updating a contact"() {
		given:
			createManyContacts()
		when:
			to PageContactShow, remote { Contact.findByName('Bob').id }
			footer.nextPage.click()
		then:
			!footer.prevPage.disabled
		when:
			singleContactDetails.name = 'Kate'
			singleContactDetails.save.click()
		then:
			!footer.prevPage.disabled
	}

	def "should display a count of messages recieved and sent for a contact"() {
		given: 'A contact has received and sent messages'
			remote {
				def sent1 = new Fmessage(inbound:false, text:"outbound 1")
				def sent2 = new Fmessage(inbound:false, text:"outbound 2")
				sent1.addToDispatches(dst:'2541234567', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true, flush:true)
				sent2.addToDispatches(dst:'2541234567', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true, flush:true)
				new Fmessage(src:'2541234567', text:"inbound 1", date: new Date(), inbound:true).save(failOnError:true, flush:true)
				null
			}
		when:
			to PageContactShow, remote { Contact.findByName('Alice').id }
		then:
			singleContactDetails.sentCount == 'contact.messages.sent[2]'
			singleContactDetails.receivedCount == 'contact.received.messages[1]'
	}

	def 'in edit mode, a focused then blurred field should appear in view mode'() { throw new RuntimeException() }

	def 'in edit mode, all fields should appear in view mode unless user focusses on them'() { throw new RuntimeException() }

	def 'in edit mode, form data should only be submitted via ajax if values are dirty'() { throw new RuntimeException() }

	def 'in edit mode, user should get success notification if contact editing was successull'() { throw new RuntimeException() }

	def 'in edit mode, user should get failure notification if contact editing failed'() { throw new RuntimeException() }

	def 'in edit mode, save and cancel button should not be visible'() { throw new RuntimeException() }

	def 'in edit mode, on hover of contact data the user should see styling that indicates editability'() { throw new RuntimeException() }

	def 'after editing a contact the user should not be redirected to another page'() { throw new RuntimeException() }

	def 'if user is adding a new contact the form data should be sent via normal POST not ajax'() { throw new RuntimeException() }
}

