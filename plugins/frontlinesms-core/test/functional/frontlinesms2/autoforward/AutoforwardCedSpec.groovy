package frontlinesms2.autoforward

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox

class AutoforwardCedSpec extends grails.plugin.geb.GebSpec{

	def "can launch autoforward wizard from create new activity link" () {
		given: ' the inbox is opened'
			to PageMessageInbox
		when: 'Activity link is clicked'
			bodyMenu.newActivity.click()
		then: 'the Create Activity Dialog is open'
			waitFor { at CreateActivityDialog }
		when: 'Autoforward is selected'
			autoforward.click()
		then: 'Create Autoforward wizard is open'
			waitFor { at AutoforwardCreateDialog }
	}

	def "Can create a new autoforward" () {
		given: 'Create Autoforward wizard is open'
			 launchAutoforwardPopup()
		when: 'Message tab is open'
			message.displayed
		then: 'Default message should be displayed'
			message.messageText == "message-content WITHOUT KEYWORD"
		when: 'Keyword is entered'
			next.click()
			keyword.keywordText = 'Hello'
			next.click()
		then: 'Recipients tab should open'
			recipients.displayed
		when: 'a recipient is added'
			recipients.addField = '1234567890'
			recipients.addButton.click()
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'HELLO'
			confirm.contacts == '1234567890'
		when: 'Submit is clicked'
			confirm.name = 'Hello'
			create.click()
		then: 'Summary should display'
			waitFor { summary.displayed }
			summary.message.contains('The autoforward has been created')
	}

	def launchAutoforwardPopup(String tab = ''){
		to PageMessageInbox
			bodyMenu.newActivity.click()
			waitFor { at CreateActivityDialog }
			autoforward.click()
			waitFor { at AutoforwardCreateDialog }
		if(tab == 'keyword'){
			next.click()
			waitFor { keyword.displayed}
		}
	}
}
