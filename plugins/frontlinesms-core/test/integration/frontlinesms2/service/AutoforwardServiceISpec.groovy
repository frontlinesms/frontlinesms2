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
			params.groups = Group.list().collect{ return "group-$it.id" } + SmartGroup.list().collect { return "smartgroup-$it.id" }
			println params.groups
		when:
			autoforwardService.editContacts(autoforward, params)
		then:
			autoforward.contacts*.mobile.containsAll(['12345', '67890'])
			autoforward.groups*.name.containsAll(['group3', 'group4','groupKawa'])
			autoforward.smartGroups*.name.containsAll(['group', 'group2','groupSmart'])
	}
}