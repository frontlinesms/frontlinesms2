package frontlinesms2.domain

import frontlinesms2.*
import spock.lang.*

class ForwardActionStepISpec extends grails.plugin.spock.IntegrationSpec {
	def "ForwardActionStep.recipient should return a list of addresses"(){
		when:
			def step = new ForwardActionStep(type:'forward')
			def contact = new Contact(name:'bob', mobile:'+234234').save()
			def contact2 = new Contact(name:'bob', mobile:'+2577').save()
			new Contact(name:'bob', mobile:'+4411').save()
			new Contact(name:'bob', mobile:'+4422').save()
			new Contact(name:'bob', mobile:'+4433').save()

			def group =  new Group(name:'my group').save()
			group.addToMembers(contact2)
			group.save()

			def smartGroup = new SmartGroup(name:'English numbers', mobile:'+44').save()

			step.addToStepProperties(new StepProperty(key:'recipient', value:"Contact-${contact.id}")) // adding contact
			step.addToStepProperties(new StepProperty(key:'recipient', value:"Group-${group.id}")) //adding group
			step.addToStepProperties(new StepProperty(key:'recipient', value:"SmartGroup-${smartGroup.id}")) //adding smart group
			step.addToStepProperties(new StepProperty(key:'recipient', value:"Address-+34567")) //adding address
		then:
			step.recipients.containsAll(["+234234", "+2577", "+4411", "+4422", "+4433", "+34567"]) 
	}
}