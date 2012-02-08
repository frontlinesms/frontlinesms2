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
				new Contact(name:it, primaryMobile:"+44789012345${++c}").save(failOnError:true, flush:true)
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
			$('#contact-header h3').text().equalsIgnoreCase('English Contacts (2)')
			at PageEnglishSmartGroupShow
	}
}

class PageEnglishSmartGroupShow extends geb.Page {
	static at = { title == 'English Contacts' }
	
	static content = {
		contactLink { $('#contact-list a') }
	}
}