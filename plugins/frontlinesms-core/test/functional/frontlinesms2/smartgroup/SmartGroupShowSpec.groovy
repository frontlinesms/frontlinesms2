package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow
import frontlinesms2.popup.*

class SmartGroupShowSpec extends SmartGroupBaseSpec {
	def 'clicking a smartgroup in the menu should load it in the view'() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			submit.click()
		then:
			at PageSmartGroup
			waitFor { bodyMenu.getSmartGroupLink('English Contacts').displayed }
		when:
			bodyMenu.getSmartGroupLink('English Contacts').click()
		then:
			at PageSmartGroup
			header.title.startsWith("english contacts")
	}
	
	def 'changing contact selection in a smartgroup should keep user in smartgroup view'() {
		given:
			def c = 0
			['Algernon', 'Bertie'].each {
				new Contact(name:it, mobile:"+44789012345${++c}").save(failOnError:true, flush:true)
			}
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			submit.click()
		then:
			at PageSmartGroup
			waitFor { bodyMenu.getSmartGroupLink('English Contacts').displayed }
		when:
			at PageSmartGroup
			bodyMenu.getSmartGroupLink('English Contacts').click()
		then:
			header.title.startsWith("english contacts")
			contactList.contact[1].displayed
		when:
			contactList.contact[1].click()
		then:
			bodyMenu.getSmartGroupLink('English Contacts').click()
	}
	
	def 'user can edit an existing smartGroup'(){
		setup:
			def englishContacts = new SmartGroup(name:'English Contacts', mobile:'+254').save(flush:true, failOnError:true)
		when:
			to PageSmartGroupShow, SmartGroup.findByName("English Contacts")
			bodyMenu.getSmartGroupLink('English Contacts').click()
		then:
			at PageSmartGroup
			header.title.startsWith('english contacts')
		when:
			header.moreGroupActions.value('edit')
			waitFor { at SmartGroupEditDialog}
			smartGroupNameField.value(smartGroupNameField.value() == englishContacts.name)
			setRuleValue(0, "+44")
			editButton.click()
			englishContacts.refresh()
		then:
			SmartGroup.count() == 1
			waitFor {englishContacts.mobile == "+44"}
	}
}

