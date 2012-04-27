package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactDeleteSpec extends ContactBaseSpec {
	
	//These are both broken because they do not appear directly on the screen, you have to scroll to see the delete button
	// if you maximize firefox when the test starts they pass
	def 'delete button is displayed and works'() {
		given:
			createTestContacts()
		when:
			to PageContactShow
			deleteSingleButton.click()
		then:
			waitFor { confirmDeleteButton.displayed }
		when:
			confirmDeleteButton.jquery.trigger("click")
		then:
			waitFor { flashMessage.displayed }
			!Contact.findByName('Alice')
	}
	
	def 'should delete multiple selected contacts'() {
		given:
			createTestContacts()
		when:
			to PageContactShowAlice
			contactSelect[1].click()
		then:
			waitFor { $('input#name').value() == 'Bob' }
		when:
			contactSelect[0].click()
		then:
			waitFor { deleteAllButton.displayed }
		when:
			deleteAllButton.click()
		then:
			waitFor { confirmDeleteButton.displayed }
		when:
			confirmDeleteButton.click()
		then:
			waitFor { flashMessage.displayed }
			Contact.count() == 0
	}
}
