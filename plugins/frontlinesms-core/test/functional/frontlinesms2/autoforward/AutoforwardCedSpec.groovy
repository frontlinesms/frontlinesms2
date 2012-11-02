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

	def "keyword must be provided in autoforward"() {
		given: 'Open keyword tab'
			 launchAutoforwardPopup('keyword')
		when: 'Keyword is entered'
			next.click()
		then: 'Error message is displayed'
			errorText == "please fill in all required fields"
			validationErrorText == "This field is required."
	}

	def "keyword must have valid commas seperated values if provided"() {
		given: 'Open keyword tab'
			 launchAutoforwardPopup('keyword')
		when: 'Keyword is entered'
			keyword.keywordText = 'Hello Goodbye'
			next.click()
		then: 'Error message is displayed'
			errorText == "please fill in all required fields"
			validationErrorText == "Keyword should not have spaces"
	}

	def "keywords must be unique if provided"() {
		given: 'Open keyword tab'
			 launchAutoforwardPopup('keyword')
		when: 'Keyword is entered'
			keyword.keywordText = 'Hello,Hello'
			next.click()
		then: 'Error message is displayed'
			errorText == "please fill in all required fields"
			validationErrorText == "Keywords must be unique"
	}

	def "keywords must not be used in another Activity"() {
		given: 'an autoforward already exists'
			createTestAutoforward()
		and: 'Open keyword tab'
			 launchAutoforwardPopup('keyword')
		when: 'Duplicate Keyword is entered'
			keyword.keywordText = 'Breaking'
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'BREAKING'
			confirm.autoforwardConfirm == "Welcome Sir/Madam. This is an autoforward response!"
		when: 'When create is clicked'
			confirm.name = 'Hello'
			create.click()
		then: 'Summary tab should NOT be displayed'
			confirm.displayed
			waitFor { errorText.contains("the keyword breaking is already in use by activity 'breakingnews'")}
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
