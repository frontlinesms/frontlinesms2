package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.ContactListPage

class SmartGroupViewSpec extends SmartGroupBaseSpec {
	def 'smart groups list is not visible if there are no smart groups'() {
		when:
			to ContactListPage
		then:
			smartGroupsListItems.size() == 0
			noSmartGroupsMessage.displayed
	}
	
	def 'smart groups list is visible if there are smart groups created'() {
		when:
			launchCreateDialog()
			ruleValues[0].value('+44')
			finishButton.click()
		then:
			waitFor { smartGroupsListItems.size() > 0 }
			!noSmartGroupsMessage.displayed	
	}
	
	def 'smart group members should be displayed when the list is clicked'() {
		when:
			launchCreateDialog()
			ruleValues[0].value('+44')
			finishButton.click()
			smartGroupsList.find('a').click()
		then:
			title.endsWith('English Contacts')
	}
	
	def 'CREATE NEW SMART GROUP button is available when there are no smart groups'() {
		when:
			to ContactListPage
		then:
			createSmartGroupButton.displayed
	}

	def 'CREATE NEW SMART GROUP button is available when there are smart groups'() {
		given:
			new SmartGroup(name:'Test Group 1', contactName:'Jeremiah').save(failOnError:true, flush:true)
		when:
			to ContactListPage
		then:
			createSmartGroupButton.displayed
	}
}