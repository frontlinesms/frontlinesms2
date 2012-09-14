package frontlinesms2.contact

import frontlinesms2.*
import frontlinesms2.popup.*
import geb.Browser
import grails.plugin.geb.GebSpec

class CustomFieldCedSpec extends ContactBaseSpec {
	
	def "selecting add custom field from dropdown opens the popup"() {
		given:
			Contact bob = Contact.build(name:'Bob')
		when:
			to PageContactShow, bob
			singleContactDetails.addMoreInfomation
		then:
			waitFor { at CustomFieldPopup }
	}

	def "should add the manually entered custom fields to the list "() {
		given:
			Contact bob = Contact.build(name:'Bob')
		when:
			to PageContactShow, bob
			singleContactDetails.addMoreInfomation
		then:
			waitFor { at CustomFieldPopup }
		when:
			newField.value('planet')
			ok.jquery.trigger("click")
		then:
			waitFor { !newField.displayed }
			at PageContactShow
			singleContactDetails.customLabel("planet").displayed
	}
}

