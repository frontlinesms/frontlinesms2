package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ContactControllerSpec extends ControllerSpec {
	def "adding and removing a contact from the same group triggers error"() {
		given:
			def c = new Contact(name:'Ada')
			mockDomain(Contact, [c])
			mockParams.id = c.id
			mockParams.groupsToAdd = ",2,"
			mockParams.groupsToRemove = ",2,"
		when:
			controller.update()
		then:
			controller.modelAndView.model.contactInstance.errors
	}
}

