package frontlinesms2.contact

import frontlinesms2.*
import java.util.regex.*

class CustomFieldCedSpec extends grails.plugin.geb.GebSpec {
	
	def "selecting add custom field from dropdown opens the popup"() {
		when:
			Contact bob = Contact.build(name:'Bob')
			go "contact/show/${bob.id}"
		then:
			at PageContactShowBob
		when:
			fieldSelecter.value('add-new').click()
		then:
			waitFor {$('div#custom-field-popup').displayed}
			$('div#custom-field-popup').displayed
	}

	def "should add the manually entered custom fields to the list "() {
		when:
			Contact bob = Contact.build(name:'Bob')
			go "contact/show/${bob.id}"
		then:
			at PageContactShowBob
		when:
			fieldSelecter.value('add-new')
		then:
			waitFor {$('div#custom-field-popup').displayed}
		when:
			$("#custom-field-name").value("planet")
			$('#done').click()
		then:
			$('#custom-field-list li').find('label').text() == "planet"
	}
}

