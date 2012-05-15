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
			at PageEnglishSmartGroupShow
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
			at PageEnglishSmartGroupShow
			contactLink[1].displayed
		then:
			contactLink[1].click()
		then:
			$('#smart-group-title').text().equalsIgnoreCase('English Contacts (2)')
			at PageEnglishSmartGroupShow
	}
	
	def 'user can edit an existing smartGroup'(){
		setup:
			def englishContacts = new SmartGroup(name:'English Contacts', mobile:'+254').save(flush:true, failOnError:true)
		when:
			to PageContactShow
			getMenuLink('English Contacts').click()
		then:
			at PageEnglishSmartGroupShow
		when:
			$("#group-actions").value("edit").jquery.trigger("click")
		then:
			waitFor { at SmartGroupEditDialog}
			smartGroupNameField.value(smartGroupNameField.value() == englishContacts.name)
		when:
			setRuleValue(0, "+44")
			editButton.click()
			englishContacts.refresh()
		then:
			SmartGroup.count() == 1
			waitFor {englishContacts.mobile == "+44"}
	}
}

class PageEnglishSmartGroupShow extends geb.Page {
	static at = { title.contains('English Contacts') }
	
	static content = {
		contactLink { $('#contact-list a') }
	}
}
