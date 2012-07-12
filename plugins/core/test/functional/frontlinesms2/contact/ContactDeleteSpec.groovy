package frontlinesms2.contact

import frontlinesms2.*
import frontlinesms2.popup.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactDeleteSpec extends ContactBaseSpec {

	def 'delete button is displayed and works'() {
		given:
			createTestContacts()
		when:
			to PageContactAll
			singleContactDetails.delete.click()
		then:
			waitFor { at DeletePopup }
		when:
			ok.jquery.trigger("click")
		then:
			at PageContactAll
			waitFor { notifications.flashMessage.displayed }
			!Contact.findByName('Alice')
	}
	
	def 'should delete multiple selected contacts'() {
		given:
			createTestContacts()
		when:
			to PageContactAll, Contact.findByName('Alice')
			contactList.selectContact 1
		then:
			waitFor { singleContactDetails.name.value() == 'Bob' }
		when:
			contactList.selectContact 0
		then:
			waitFor { multipleContactDetails.delete.displayed  }
		when:
			multipleContactDetails.delete.click()
		then:
			waitFor { at DeletePopup }
		when:
			ok.jquery.trigger("click")
		then:
			at PageContactAll
			waitFor { notifications.flashMessage.displayed }
			Contact.count() == 0
	}
}
