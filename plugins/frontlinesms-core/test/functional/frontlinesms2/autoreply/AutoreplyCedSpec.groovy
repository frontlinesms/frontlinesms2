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
			to PageMessageInbox
			bodyMenu.newActivity.click()
			waitFor { at CreateActivityDialog }
			autoreply.click()
			waitFor { at AutoreplyCreateDialog }
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
}
