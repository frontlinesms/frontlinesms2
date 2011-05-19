package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ContactControllerSpec extends ControllerSpec {
	def "adding and removing a contact from the same group triggers error"() {
		given:
			mockDomain(Contact, [new Contact(name:'Ada')])
			mockParams.id = 1
			mockParams.groupsToAdd = ",2,"
			mockParams.groupsToRemove = ",2,"
		when:
			controller.update()
		then:
			controller.modelAndView.model.contactInstance.errors
	}
}

