package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow

abstract class SmartGroupBaseSpec extends grails.plugin.geb.GebSpec {
	def removeRule(i) {
		int ruleCount = rules.size()
		if(removeRuleButtons[i].displayed && !removeRuleButtons[i].disabled) {
			removeRuleButtons[i].click()
			waitFor { rules.size() == ruleCount-1 }
		}
	}
	
	def setRuleValue(i, val) {
		ruleValues[i].value(val)
	}
	
	def launchCreateDialog(smartGroupName='English Contacts') {
		to PageContactShow
		createSmartGroupButton.click()
		waitFor { at SmartGroupCreateDialog }
		if(smartGroupName) smartGroupNameField.value(smartGroupName)
	}

	def addRule() {
		int ruleCount = rules.size()
		addRuleButton.click()
		waitFor { rules.size() == ruleCount+1 }
	}
	
	def getMenuLink(smartGroupName) {
		$('#smart-groups-submenu li a', text:smartGroupName)
	}
}