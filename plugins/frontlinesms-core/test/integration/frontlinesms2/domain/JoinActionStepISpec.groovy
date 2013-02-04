package frontlinesms2.domain

import frontlinesms2.*
import spock.lang.*

class JoinActionStepISpec extends grails.plugin.spock.IntegrationSpec {
	def "Can retrieve Contacts stored as StepProperties"() {
			given:
				def customActivity = CustomActivity.build()
				def step = new JoinActionStep(type: 'joinAction')
				customActivity.addToSteps(step)
				customActivity.save(failOnError:true)
				def contactList = []
				(1..20).each {
					contactList << new Contact(name:"test-${it}", mobile:"number-${it}").save(failOnError:true)
				}
				// to make it pass validation
				step.addToStepProperties(new StepProperty(key:"group", value:"invaluable")).save(failOnError:true)
				contactList.each {
					step.addToStepProperties(new StepProperty(key:'contactId', value:it.id))
				}
				(11121..11130).each {
					// add ids for non-existant contactIds to simulate deleted contacts
					step.addToStepProperties(new StepProperty(key:'contactId', value:it))
				}
				step.save(flush:true, failOnError:true)
			when:
				println "STEP PROPERTIES :::: ${step.stepProperties}"
				def retrievedContacts = step.getEntityList(Contact, "contactId")
			then:
				retrievedContacts.sort { it.id } == contactList
	   }
}
