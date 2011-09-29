package frontlinesms2.contact

import frontlinesms2.*

class SmartGroupFSpec extends grails.plugin.geb.GebSpec {
	def 'smart groups list is not visible if there are no smart groups'() {
		when:
			to ContactsPage
		then:
			!smartGroupsList.displayed
			noSmartGroupsMessage.displayed
	}
	
	def 'CREATE NEW SMART GROUP button is available when there are no smart groups'() {
		when:
			to ContactsPage
		then:
			createSmartGroupButton.displayed
	}

	def 'CREATE NEW SMART GROUP button is available when there are smart groups'() {
		given:
			new SmartGroup(name:'Test Group 1', contactName:'Jeremiah').save(failOnError:true, flush:true)
		when:
			to ContactsPage
		then:
			createSmartGroupButton.displayed
	}
	
	def 'Add More Rules button is visible'() {
		when:
			launchCreateDialog()
		then:
			addRuleButton.displayed
	}
	
	def 'One rule is created by default'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
	}

	def 'FINISH button is disabled by default'() {
		when:
			launchCreateDialog()
		then:
			finishButton.disabled
	}
	
	def 'there is no BACK button'() {
		when:
			launchCreateDialog()
		then:
			!$('.button', text:'Back').displayed
	}
	
	def 'CANCEL button removes dialog'() {
		when:
			launchCreateDialog()
			cancelButton.click()
		then:
			waitFor { !at(CreateSmartGroupDialog) }
	}
	
	def 'Add more rules button will add more rules'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
		when:		
			setRuleValue(0, '+44')
			addRuleButton.click()
		then:
			rules.size() == 2
		when:		
			setRuleValue(1, 'boris')
			addRuleButton.click()
		then:
			rules.size() == 3
	}
	
	def 'filling in rule details will enable the FINISH button'() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
		then:
			waitFor { finishButton.enabled }
	}
	
	def 'cannot add new rule when previous rule does not validate'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
		when:
			addRuleButton.click()
		then:
			rules.size() == 1
	}

	def "there is no remove button for first rule, even when other rules are displayed"() {
		when:
			launchCreateDialog()
		then:
			!removeRuleButtons[0].displayed
		when:
			setRuleValue(0, '+44')
			addRuleButton.click()
		then:
			!removeRuleButtons[0].displayed
			removeRuleButtons[1].displayed
	}

	def "can remove old rule if it's not the first"() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			addRuleButton.click()
		then:
			rules.size() == 2
		when:
			removeRule(1)
		then:
			rules.size() == 1
	}
	
	private def removeRule(i) {
		int ruleCount = rules.size()
		removeRuleButtons[i].click()
		waitFor { rules.size() == ruleCount-1 }
	}
	
	private def setRuleValue(i, val) {
		ruleValues[i].val(val)
	}
	
	private def launchCreateDialog() {
		to ContactsPage
		createSmartGroupButton.click()
		waitFor { at CreateSmartGroupDialog }
	}
}

class CreateSmartGroupDialog extends geb.Page {
	static at = {
		$("#ui-dialog-title-modalBox").text() == 'Create group'
	}
	
	static content = {
		rules { $('ul#smart-group-criteria li') }
		ruleValues { rules.find('input', type:'textfield') }
		removeRuleButtons { rules.find('.button.remove-rule') }
		
		addRuleButton { $('.button', text:"Add more rules") }
		finishButton { $('.button', text:'Finish') }
	}
}