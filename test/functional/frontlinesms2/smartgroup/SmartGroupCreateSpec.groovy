package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow

class SmartGroupCreateSpec extends SmartGroupBaseSpec {
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
			!finishButton.disabled
	}
	
	def 'there is no BACK button'() {
		when:
			launchCreateDialog()
		then:
			backButton.disabled
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
			smartGroupNameField.displayed
	}
	
	def 'error message is not displayed by default'() {
		when:
			launchCreateDialog(null)
		then:
			!errorMessages.displayed
	}
	
	def 'Clicking FINISH when no name is defined should display validation error'() {
		when:
			launchCreateDialog(null)
			finishButton.click()
		then:
			waitFor { errorMessages.displayed }
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
			ruleField[0].value() == 'mobile'
			ruleMatchText[0] == 'starts with'
	}
	
	def 'selecting fields other than PHONE NUMBER should set matcher text to CONTAINS'() {
		when:
			launchCreateDialog()
		then:
			ruleField[0].value() == 'mobile'
			ruleMatchText[0] == 'starts with'
		when:
			ruleField[0].value('Contact name')
		then:
			ruleMatchText[0] == 'contains'
		when:
			ruleField[0].value('Phone number')
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
			waitFor { errorMessages.displayed }
	}

	def 'a single empty rule should fail validation'() {
		when:
			launchCreateDialog()
			finishButton.click()
		then:
			waitFor { errorMessages.displayed }
	}

	def 'a single empty rule followed by filled rules should fail validation'() {
		when:
			launchCreateDialog()
			addRule()
			ruleField[1].value('Contact name')
			ruleValues[1].value('bob')
			finishButton.click()
		then:
			waitFor { errorMessages.displayed }
	}
	
	def 'filled rule followed by empty rule should fail validation'() {
		when:
			launchCreateDialog()
			addRule()
			ruleField[1].value('Contact name')
			ruleValues[1].value('bob')
			finishButton.click()
		then:
			waitFor { errorMessages.displayed }
	}
	
	def 'successfully creating a smart group should show a flash message'() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			finishButton.click()
		then:
			waitFor { flashMessage.text() == "Created new smart group: 'English Contacts'" }
	}

	def 'successfully creating a smart group should add it to the smart groups menu'() {
		when:
			to PageContactShow
		then:
			!getMenuLink('All the bobs!').displayed
		when:
			launchCreateDialog('All the bobs!')
			ruleField[0].value('Contact name')
			setRuleValue(0, 'bob')
			finishButton.click()
		then:
			waitFor { getMenuLink('All the bobs!').displayed }
	}
}