package frontlinesms2.contact

import frontlinesms2.*
import java.util.regex.*

class CustomFieldCedSpec extends grails.plugin.geb.GebSpec {
	
	def "selecting add custom field from dropdown opens the popup"() {
		when:
			Contact bob = new Contact(name:'Bob').save(failOnError: true, flush: true)
			go "contact/show/${bob.id}"
			$("#new-field-dropdown").value('add-new')
			waitFor {$('div#custom-field-popup').displayed}
		then:
			$('div#custom-field-popup').displayed
	}

	def "should add the manually entered custom fields to the list "() {
		when:
			Contact bob = new Contact(name:'Bob').save(failOnError: true, flush: true)
			go "contact/show/${bob.id}"
			$("#new-field-dropdown").value('add-new')
		then:
			waitFor {$('div#custom-field-popup').displayed}
		
			$("#custom-field-name").value("planet")
			def btnDone = $('#done')
			btnDone.click()
		then:
			$('#custom-field-list li').find('label').text() == "planet"
	}
}

