package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.ContactListPage

abstract class SmartGroupBaseSpec extends grails.plugin.geb.GebSpec {
	def removeRule(i) {
		int ruleCount = rules.size()
		removeRuleButtons[i].click()
		waitFor { rules.size() == ruleCount-1 }
	}
	
	def setRuleValue(i, val) {
		ruleValues[i].value(val)
	}
	
	def launchCreateDialog(smartGroupName='English Contacts') {
		to ContactListPage
		createSmartGroupButton.click()
		waitFor { at SmartGroupCreateDialog }
		if(smartGroupName) smartGroupNameField.value(smartGroupName)
	}

	def addRule() {
		int ruleCount = rules.size()
		addRuleButton.click()
		waitFor { rules.size() == ruleCount+1 }
	}
}