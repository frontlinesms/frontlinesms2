package frontlinesms2.smartgroup

class SmartGroupCreateSpec extends grails.plugin.geb.GebSpec {
	def 'ADD MORE RULES button is visible in CREATE dialog'() {
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

	def 'FINISH button is enabled by default'() {
		when:
			launchCreateDialog()
		then:
			finishButton.enabled
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
			waitFor { !at(SmartGroupCreateDialog) }
	}
	
	def 'SMART GROUP NAME FIELD is displayed'() {
		when:
			launchCreateDialog(null)
		then:
			nameField.displayed
	}
	
	def 'error message is not displayed by default'() {
		when:
			launchCreateDialog(null)
		then:
			!errorMessage.displayed
	}
	
	def 'Clicking FINISH when no name is defined should display validation error'() {
		when:
			launchCreateDialog(null)
			finishButton.click()
		then:
			waitFor { errorMessage.displayed }
	}
	
	def 'ADD MORE RULES button should add one more rule'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
		when:		
			addRule()
		then:
			rules.size() == 2
		when:
			addRule()
		then:
			rules.size() == 3
	}
	
	def 'can add new rule when previous rule does not validate'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
		when:
			addRule()
		then:
			rules.size() == 2
	}

	def "there is no remove button for first rule, except when other rules are displayed"() {
		when:
			launchCreateDialog()
		then:
			!removeRuleButtons[0].displayed
		when:
			addRule()
		then:
			removeRuleButtons[0].displayed
			removeRuleButtons[1].displayed
	}

	def "can remove old rule if it's not the first"() {
		when:
			launchCreateDialog()
			addRule()
		then:
			rules.size() == 2
		when:
			removeRule(1)
		then:
			rules.size() == 1
	}
	
	def "can remove first rule if there are other rules"() {
		when:
			launchCreateDialog()
			addRule()
		then:
			rules.size() == 2
		when:
			removeRule(0)
		then:
			rules.size() == 1
	}
	
	def "cannot remove lone first rule even if it was previously not first rule"() {
		when:
			launchCreateDialog()
			addRule()
		then:
			rules.size() == 2
		when:
			removeRule(0)
		then:
			rules.size() == 1
		when:
			removeRule(0)
		then:
			rules.size() == 1
	}
	
	def 'selecting PHONE NUMBER should set matcher text to STARTS WITH'() {
		when:
			launchCreateDialog()
		then:
			ruleField[0].value() == 'Phone Number'
			ruleMatchText[0] == 'starts with'
	}
	
	def 'selecting fields other than PHONE NUMBER should set matcher text to CONTAINS'() {
		when:
			launchCreateDialog()
		then:
			ruleField[0].value() == 'Phone Number'
			ruleMatchText[0] == 'starts with'
		when:
			ruleField[0].value('Name')
		then:
			ruleMatchText[0] == 'contains'
		when:
			ruleField[0].value('Phone Number')
		then:
			ruleMatchText[0] == 'starts with'
	}
	
	def 'adding multiple rules on the same field should fail validation'() {
		when:
			launchCreateDialog()
			ruleValues[0].value('+44')
			addRule()
			ruleValues[1].value('+254')
			finishButton.click()
		then:
			waitFor { errorMessage.displayed }
	}
	
	def 'successfully creating a smart group should show a flash message'() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			finishButton.click()
		then:
			waitFor { flashMessage.text() == "Created new smart group 'English Contacts'" }
	}
	
	private def removeRule(i) {
		int ruleCount = rules.size()
		removeRuleButtons[i].click()
		waitFor { rules.size() == ruleCount-1 }
	}
	
	private def setRuleValue(i, val) {
		ruleValues[i].value(val)
	}
	
	private def launchCreateDialog(smartGroupName='English Contacts') {
		to ContactsPage
		createSmartGroupButton.click()
		waitFor { at SmartGroupCreateDialog }
		if(smartGroupName) smartGroupNameField.value(smartGroupName)
	}

	private def addRule() {
		int ruleCount = rules.size()
		addRuleButton.click()
		waitFor { rules.size() == ruleCount+1 }
	}
}