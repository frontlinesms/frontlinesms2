package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow

class SmartGroupShowSpec extends SmartGroupBaseSpec {
	def 'clicking a smart group in the menu should load it in the view'() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			finishButton.click()
		then:
			waitFor { getMenuLink('English Contacts').displayed }
		when:
			getMenuLink('English Contacts').click()
		then:
			title == 'English Contacts'
	}
}