package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec
import frontlinesms2.message.*

class ContactEditSpec extends ContactBaseSpec {

	def setup() {
		createTestContacts()
		remote {
			def grailsApplication = org.codehaus.groovy.grails.commons.ApplicationHolder.application
			grailsApplication.mainContext.appSettingsService.set("non.numeric.characters.removed.warning.disabled", 'some placeholder value')
			grailsApplication.mainContext.appSettingsService.set("international.number.format.warning.disabled", 'some placeholder value')
		}
	}

	def 'selected contact details can be edited and saved, which updates contact list values'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
		when:
			to PageContactShow, aliceId

			singleContactDetails.name = 'Kate'
			singleContactDetails.name().jquery.blur()
		then:
			waitFor { remote { Contact.findById(aliceId).name } == 'Kate' }
		when:
			singleContactDetails.mobile = '+254987654321'
			singleContactDetails.mobile().jquery.blur()
		then:
			waitFor { remote { Contact.findById(aliceId).mobile } == '+254987654321' }
		when:
			singleContactDetails.email = 'gaga@gmail.com'
			singleContactDetails.email().jquery.blur()
		then:
			waitFor { remote { Contact.findById(aliceId).email } == 'gaga@gmail.com' }
		and:
			assertFieldDetailsCorrect('name', 'Name', 'Kate')
			assertFieldDetailsCorrect('mobile', 'Mobile', '+254987654321')
	}

	def "should disable the save and cancel buttons when viewing a contact details"() {
		when:
			to PageContactShow, remote { Contact.findByName('Bob').id }
		then:
			singleContactDetails.save.disabled
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
			singleContactDetails.mobile.click()
		then:
			!footer.prevPage.disabled
	}

	def "should display a count of messages recieved and sent for a contact"() {
		given: 'A contact has received and sent messages'
			remote {
				def sent1 = new TextMessage(inbound:false, text:"outbound 1")
				def sent2 = new TextMessage(inbound:false, text:"outbound 2")
				sent1.addToDispatches(dst:'+2541234567', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true, flush:true)
				sent2.addToDispatches(dst:'+2541234567', status:DispatchStatus.SENT, dateSent:new Date()).save(failOnError:true, flush:true)
				new TextMessage(src:'+2541234567', text:"inbound 1", date: new Date(), inbound:true).save(failOnError:true, flush:true)
				null
			}
		when:
			to PageContactShow, remote { Contact.findByName('Alice').id }
		then:
			singleContactDetails.sentCount == 'contact.messages.sent[2]'
			singleContactDetails.receivedCount == 'contact.received.messages[1]'
	}

	def 'using a non-internationalised number should display a warning'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
		when:
			to PageContactShow, aliceId

			singleContactDetails.name.value('Kate')
			singleContactDetails.name().jquery.blur()
		then:
			waitFor { remote { Contact.findById(aliceId).name } == 'Kate' }
		when:
			singleContactDetails.mobile.value('11111')
			singleContactDetails.mobile.jquery.trigger('change')
			singleContactDetails.mobile.jquery.trigger('blur')
		then:
			waitFor {
				singleContactDetails.nonInternationalNumberWarning.displayed
			}
	}

	def 'once non-internationalised number warning is dismissed, it does not appear again'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
		when:
			to PageContactShow, aliceId

			singleContactDetails.name.value('Kate')
			singleContactDetails.name().jquery.blur()
		then:
			waitFor { remote { Contact.findById(aliceId).name } == 'Kate' }
		when:
			singleContactDetails.mobile.value('11111')
			singleContactDetails.mobile.jquery.trigger('change')
			singleContactDetails.mobile.jquery.trigger('blur')
		then:
			singleContactDetails.nonInternationalNumberWarning.displayed
		when:
			singleContactDetails.dismissNonInternationalNumberWarning.click()
		then:
			waitFor {
				!singleContactDetails.nonInternationalNumberWarning.displayed
			}
		when:
			to PageContactShow, aliceId
		then:
			!singleContactDetails.nonInternationalNumberWarning.displayed
	}
	
	def 'contact fields in the list are not updated if save was unsuccessful (name edit)'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
			to PageContactShow, aliceId
		when:
			singleContactDetails.mobile = ''
			singleContactDetails.mobile().jquery.blur()
		then:
			waitFor {
				!contactList.selectedContact?.text()?.contains('+2541234567')
			}
		when:
			singleContactDetails.name = ''
			singleContactDetails.name().jquery.blur()
		then:
			waitFor {
				remote {
					def c = Contact.findById(aliceId)
					[c.name, c.mobile]
				} == ['Alice', null]
			}
			contactList.selectedContact?.text()?.contains('Alice')
	}

	def 'contact fields in the list are not updated if save was unsuccessful (mobile number edit)'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
			to PageContactShow, aliceId
		when:
			singleContactDetails.name = ''
			singleContactDetails.name().jquery.blur()
		then:
			waitFor {
				!contactList.selectedContact?.text()?.contains('Alice')
			}
		when:
			singleContactDetails.mobile = ''
			singleContactDetails.mobile().jquery.blur()
		then:
			waitFor {
				remote {
					def c = Contact.findById(aliceId)
					[c.name, c.mobile]
				} == ['', '+2541234567']
			}
			contactList.selectedContact?.text()?.contains('+2541234567')
	}

	def 'contact flags are displayed and updated on save'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
		when:
			to PageContactShow, aliceId
		then:
			singleContactDetails.flagClasses.contains('flag-ke')
		when:
			singleContactDetails.mobile = '+447943444444'
			singleContactDetails.mobile().jquery.blur()
		then:
			waitFor { singleContactDetails.flagClasses.contains('flag-gb') }
		when:
			singleContactDetails.mobile = '+111'
			singleContactDetails.mobile().jquery.blur()
		then:
			waitFor { singleContactDetails.flagClasses.contains('flag-frontlinesms') }
	}

	def 'entering non-numeric characters in mobile number should display a warning'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
		when:
			to PageContactShow, aliceId

			singleContactDetails.name.value('Kate')
			singleContactDetails.name().jquery.blur()
		then:
			waitFor { remote { Contact.findById(aliceId).name } == 'Kate' }
		when:
			singleContactDetails.mobile.value('11111sdd')
			singleContactDetails.mobile.jquery.trigger('keyup')
		then:
			waitFor {
				singleContactDetails.nonNumericCharacterWarning.displayed
			}
	}

	def 'once non-numeric characters warning is dismissed, it does not appear again'() {
		given:
			def aliceId = remote { Contact.findByName('Alice').id }
		when:
			to PageContactShow, aliceId
			singleContactDetails.mobile.value('11111dff')
			singleContactDetails.mobile.jquery.trigger('keyup')
			singleContactDetails.mobile.jquery.trigger('change')
			singleContactDetails.mobile.jquery.trigger('blur')
		then:
			singleContactDetails.nonNumericCharacterWarning.displayed
		when:
			singleContactDetails.dismissNonNumericCharacterWarning.click()
		then:
			waitFor {
				!singleContactDetails.nonNumericCharacterWarning.displayed
			}
		when:
			to PageContactShow, aliceId
		then:
			!singleContactDetails.nonNumericCharacterWarning.displayed
	}
}

