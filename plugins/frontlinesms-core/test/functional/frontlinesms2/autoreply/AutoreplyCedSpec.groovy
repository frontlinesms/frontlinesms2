package frontlinesms2.autoreply

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox

class AutoreplyCedSpec extends AutoreplyBaseSpec{

	def "can launch autoreply wizard from create new activity link" () {
		given: ' the inbox is opened'
			to PageMessageInbox
		when: 'Activity link is clicked'
			bodyMenu.newActivity.click()
		then: 'the Create Activity Dialog is open'
			waitFor { at CreateActivityDialog }
		when: 'Autoreply is selected'
			autoreply.click()
		then: 'Create Autoreply wizard is open'
			waitFor { at AutoreplyCreateDialog }
	}

	def "Can create a new autoreply" () {
		given: 'Create Autoreply wizard is open'
			 launchAutoreplyPopup()
		when: 'Text message is entered'
			messageText = "Welcome Sir/Madam. This is an autoreply response!"
			next.click()
		then: 'Keyword tab should open'
			keyword.displayed
		when: 'Keyword is entered'
			keyword.keywordText = 'Hello'
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'HELLO'
			confirm.autoreplyConfirm == "Welcome Sir/Madam. This is an autoreply response!"
		when: 'When create is clicked'
			confirm.name = 'Hello'
			create.click()
		then: 'Summary tab should open'
			waitFor { summary.displayed }
			summary.message.contains('The autoreply has been created')
	}

	def "Can edit an existing autoreply"() {
		given:'create an autoreply'
			createTestAutoreply()
		when: 'go to autoreply message page'
			to PageMessageAutoreply, 'Fruits'
		then: 'autoreply message page should be open'
			header.title == 'fruits autoreply'
		when: 'edit button is clicked'
			moreActions.value("edit").click()
		then: 'autoreply wizard should open'
			waitFor { at AutoreplyCreateDialog }
		when: 'Text messages is edited'
			messageText = 'Some other text'
			next.click()
		then: 'Keyword tab should open'
			keyword.displayed
		when: 'Keyword is changed'
			keyword.keywordText = 'Goodbye'
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'GOODBYE'
			confirm.autoreplyConfirm == "Some other text"
		when: 'submit is clicked'
			confirm.name = 'Hello'
			create.click()
		then: 'Summary tab should open'
			waitFor { summary.displayed }
			summary.message.contains('The autoreply has been created')
	}

	def "keyword must be provided in autoreply"() {
		given: 'Open keyword tab'
			 launchAutoreplyPopup('keyword')
		when: 'Keyword is entered'
			next.click()
		then: 'Error message is displayed'
			errorText == "please fill in all required fields"
			validationErrorText == "This field is required."
	}

	def "keyword must have valid commas seperated values if provided"() {
		given: 'Open keyword tab'
			 launchAutoreplyPopup('keyword')
		when: 'Keyword is entered'
			keyword.keywordText = 'Hello Goodbye'
			next.click()
		then: 'Error message is displayed'
			errorText == "please fill in all required fields"
			validationErrorText == "Keyword should not have spaces"
	}

	def "keywords must be unique if provided"() {
		given: 'Open keyword tab'
			 launchAutoreplyPopup('keyword')
		when: 'Keyword is entered'
			keyword.keywordText = 'Hello,Hello'
			next.click()
		then: 'Error message is displayed'
			errorText == "please fill in all required fields"
			validationErrorText == "Keywords must be unique"
	}

	def "keywords must not be used in another Activity"() {
		given: 'an autoreply already exists'
			createTestAutoreply()
		and: 'Open keyword tab'
			 launchAutoreplyPopup('keyword')
		when: 'Duplicate Keyword is entered'
			keyword.keywordText = 'Mango'
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'MANGO'
			confirm.autoreplyConfirm == "Welcome Sir/Madam. This is an autoreply response!"
		when: 'When create is clicked'
			confirm.name = 'Hello'
			create.click()
		then: 'Summary tab should NOT be displayed'
			confirm.displayed
			waitFor { errorText.contains("the keyword mango is already in use by activity 'fruits'")}
	}

	def launchAutoreplyPopup(String tab = ''){
		to PageMessageInbox
			bodyMenu.newActivity.click()
			waitFor { at CreateActivityDialog }
			autoreply.click()
			waitFor { at AutoreplyCreateDialog }
		if(tab == 'keyword')
			messageText = "Welcome Sir/Madam. This is an autoreply response!"
			next.click()
	}
}
