package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class AutoforwardServiceISpec extends grails.plugin.spock.IntegrationSpec{
	def autoforwardService

	def 'editing the contacts in Autoforward should work'(){
		setup:
			def autoforward = new Autoforward(name:'Excitement', sentMessageText:'This is exciting: ${messageText}')
			.addToKeywords(value:'FORWARD')
			.addToGroups(name:'groupKawa')
			.addToSmartGroups(name:'groupSmart', mobile:'+257')
			.save(failOnError:true, flush:true)
			new Contact(mobile:'12345', name:'').save(failOnError:true)
			new Contact(mobile:'67890', name:'').save(failOnError:true)
			new SmartGroup(name:'group', mobile:'+254').save(failOnError:true)
			new SmartGroup(name:'group2', mobile:'+256').save(failOnError:true)
			new Group(name:'group3').save(failOnError:true)
			new Group(name:'group4').save(failOnError:true)
			def params = [:]
			params.addresses = ['12345','67890']
			params.groups = ["group-${Group.findByName('group3').id}", "group-${Group.findByName('group4').id}", "smartgroup-${SmartGroup.findByName('group').id}", "smartgroup-${SmartGroup.findByName('group2').id}"]
			println params.groups
		when:
			autoforwardService.editContacts(autoforward, params)
		then:
			autoforward.contacts*.mobile.containsAll(['12345', '67890'])
			autoforward.groups*.name.containsAll(['group3', 'group4'])
			!autoforward.groups*.name.contains('groupKawa')
			autoforward.smartGroups*.name.containsAll(['group', 'group2'])
			!autoforward.smartGroups*.name.contains('groupSmart')
	}

	def 'deleting a contact should remove it from an autoforward'() {
		given:
			def c1 = new Contact(mobile:'12345', name:'').save(failOnError:true)
			def c2 = new Contact(mobile:'67890', name:'').save(failOnError:true)
			def autoforward = new Autoforward(name:'Excitement', sentMessageText:'This is exciting: ${messageText}')
			.addToKeywords(value:'FORWARD')
			.addToContacts(c1)
			.addToContacts(c2)
			.save(failOnError:true, flush:true)
		when:
			autoforwardService.handleDeletedContact(c1)
			autoforward.refresh()
		then:
			!autoforward.contacts.contains(c1)
	}

	def 'deleting a group should remove it from an autoforward'() {
		given:
			def g1 = new Group(name:'group3').save(failOnError:true)
			def g2 = new Group(name:'group4').save(failOnError:true)
			def autoforward = new Autoforward(name:'Excitement', sentMessageText:'This is exciting: ${messageText}')
			.addToKeywords(value:'FORWARD')
			.addToGroups(g1)
			.addToGroups(g2)
			.save(failOnError:true, flush:true)
		when:
			autoforwardService.handleDeletedGroup(g1)
			autoforward.refresh()
		then:
			!autoforward.groups.contains(g1)
	}

	def 'deleting a smart group should remove it from an autoforward'() {
		given:
			def sg1  = new SmartGroup(name:'group', mobile:'+254').save(failOnError:true)
			def sg2 = new SmartGroup(name:'group2', mobile:'+256').save(failOnError:true)
			def autoforward = new Autoforward(name:'Excitement', sentMessageText:'This is exciting: ${messageText}')
			.addToKeywords(value:'FORWARD')
			.addToSmartGroups(sg1)
			.addToSmartGroups(sg2)
			.save(failOnError:true, flush:true)
		when:
			autoforwardService.handleDeletedSmartGroup(sg1)
			autoforward.refresh()
		then:
			!autoforward.smartGroups.contains(sg1)
	}
}