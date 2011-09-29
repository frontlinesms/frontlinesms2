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
			addMoreRulesButton.displayed
	}
	
	def 'One criteria is created by default'() {
		when:
			launchCreateDialog()
		then:
			criteriaRows.size() == 1
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
		criteriaRows { $('ul#smart-group-criteria li') }
		addMoreRulesButton { $('.button', text:"Add more rules") }
		finishButton { $('.button', text:'Finish') }
	}
}