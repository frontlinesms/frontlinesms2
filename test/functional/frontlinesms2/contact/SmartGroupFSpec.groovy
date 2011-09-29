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
			waitFor { !at CreateSmartGroupDialog }
	}
	
	def 'Add more rules button will add more rules'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
		when:	
			rules[0].val('+44')
			addRuleButton.click()
		then:
			rules.size() == 2
		when:	
			rules[0].val('boris')
			addRuleButton.click()
		then:
			rules.size() == 3
	}
	
	def 'filling in rule details will enable the FINISH button'() {
		when:
			launchCreateDialog()
			rules[0].val('+44')
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
	
	private def launchCreateDialog() {
		to ContactsPage
		createSmartGroupButton.click()
		waitFor { at CreateSmartGroupDialog }
	}
}

class CreateSmartGroupDialog extends geb.Page {
	at = {
		$("#ui-dialog-title-modalBox").text() == 'Create group'
	}
	
	content = {
		rules { $('ul#smart-group-criteria li') }
		addRuleButton { $('.button', text:"Add more rules") }
		finishButton { $('.button', text:'Finish') }
	}
}