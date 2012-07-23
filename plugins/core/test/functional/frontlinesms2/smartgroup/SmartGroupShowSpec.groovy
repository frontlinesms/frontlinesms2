package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow

class SmartGroupShowSpec extends SmartGroupBaseSpec {
	def 'clicking a smartgroup in the menu should load it in the view'() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			finishButton.click()
		then:
			waitFor { getMenuLink('English Contacts').displayed }
		when:
			getMenuLink('English Contacts').click()
		then:
			smartGroupIsDisplayed(SmartGroup.findByName("English Contacts"))
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
			finishButton.click()
		then:
			waitFor { getMenuLink('English Contacts').displayed }
		when:
			getMenuLink('English Contacts').click()
		then:
			smartGroupIsDisplayed(SmartGroup.findByName("English Contacts"))
			contactLink[1].displayed
		then:
			contactLink[1].click()
		then:
			smartGroupIsDisplayed(SmartGroup.findByName("English Contacts"))
	}
	
	def 'user can edit an existing smartGroup'(){
		setup:
			def englishContacts = new SmartGroup(name:'English Contacts', mobile:'+254').save(flush:true, failOnError:true)
		when:
			to PageSmartGroupShow, SmartGroup.findByName("English Contacts")
			getMenuLink('English Contacts').click()
		then:
			smartGroupIsDisplayed(SmartGroup.findByName("English Contacts"))
		when:
			moreActionsSelect("edit")
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
