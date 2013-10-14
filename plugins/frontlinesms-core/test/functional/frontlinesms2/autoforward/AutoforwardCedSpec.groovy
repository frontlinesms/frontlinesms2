package frontlinesms2.autoforward

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.popup.*
import spock.lang.*
import frontlinesms2.message.PageMessageInbox

class AutoforwardCedSpec extends AutoforwardBaseSpec{
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
			message.messageText.value('message to send')
			next.click()
			keyword.keywordText = 'Hello'
			next.click()
		then: 'Recipients tab should open'
			recipients.displayed
		when: 'a recipient is added'
			recipients.addRecipient('1234567890')
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'HELLO'
			//confirm.contacts == '1234567890'
		when: 'Submit is clicked'
			confirm.nameText.value('Hello')
			create.click()
		then: 'Summary should display'
			waitFor { summary.displayed }
			summary.message.jquery.html().contains('autoforward.info')
	}

	def "Can edit an existing autoforward"() {
		given:'create an autoforward'
			createTestAutoforward()
		when: 'go to autoforward message page'
			to PageMessageAutoforward, 'News'
		then: 'autoforward message page should be open'
			header.title == 'autoforward.title[news]'
		when: 'edit button is clicked'
			header.moreActions.value("edit").click()
		then: 'autoforward wizard should open'
			waitFor { at AutoforwardCreateDialog }
		when: 'message tab is skipped'
			next.click()
		then: 'Keyword tab should open'
			keyword.displayed
		when: 'Keyword is changed'
			keyword.keywordText = 'Goodbye'
			next.click()
		then: 'Recipients tab should open'
			recipients.displayed
		when: 'A contact is added'
			recipients.addRecipient('1234567890')
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'GOODBYE'
		when: 'submit is clicked'
			confirm.nameText.value('Hello')
			create.click()
		then: 'Summary tab should open'
			waitFor { summary.displayed }
			summary.message.jquery.html().contains('autoforward.info')
	}

	def "keyword must be provided in autoforward"() {
		given: 'Open keyword tab'
			 launchAutoforwardPopup('keyword')
		when: 'Keyword is entered'
			next.click()
		then: 'Error message is displayed'
			errorText == "activity.validation.prompt"
			validationErrorText == "jquery.validation.required"
	}

	def "keyword must have valid commas seperated values if provided"() {
		given: 'Open keyword tab'
			 launchAutoforwardPopup('keyword')
		when: 'Keyword is entered'
			keyword.keywordText = 'Hello Goodbye'
			next.click()
		then: 'Error message is displayed'
			errorText == "activity.validation.prompt"
			validationErrorText == "validation.nospaces.error"
	}

	def "keywords must be unique if provided"() {
		given: 'Open keyword tab'
			 launchAutoforwardPopup('keyword')
		when: 'Keyword is entered'
			keyword.keywordText = 'Hello,Hello'
			next.click()
		then: 'Error message is displayed'
			errorText == "activity.validation.prompt"
			validationErrorText == "activity.generic.sort.validation.unique.error"
	}

	def "keywords must not be used in another Activity"() {
		given: 'an autoforward already exists'
			createTestAutoforward()
		and: 'Open keyword tab'
			 launchAutoforwardPopup('keyword')
		when: 'Duplicate Keyword is entered'
			keyword.keywordText = 'Breaking'
			next.click()
			recipients.addRecipient('1234567890')
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'BREAKING'
			//confirm.autoforwardConfirm == "Welcome Sir/Madam. This is an autoforward response!"
		when: 'When create is clicked'
			confirm.nameText.value('Hello')
			create.click()
		then: 'Summary tab should NOT be displayed'
			confirm.displayed
			waitFor { errorText.contains("activity.generic.keyword.in.use[breaking,'news']")}
	}

	def 'contact and groups of an autoforward should be preselected duting editing'(){
		given:'create an autoforward'
			createTestAutoforward()
		when: 'go to autoforward message page'
			to PageMessageAutoforward, 'News'
		then: 'autoforward message page should be open'
			header.title == 'autoforward.title[news]'
		when: 'edit button is clicked'
			header.moreActions.value("edit").click()
		then: 'autoforward wizard should open'
			waitFor { at AutoforwardCreateDialog }
		when: 'message tab is skipped'
			next.click()
		then: 'Keyword tab should open'
			keyword.displayed
		when: 'Keyword is changed'
			keyword.keywordText = 'Goodbye'
			next.click()
		then: 'Contacts should be selected'
			println "############# RECIPIENTS::: ${recipients.getRecipients()}"
			recipients.getRecipients('contact').containsAll((1..10).collect { mob -> remote { Contact.findByMobile(mob).id }.toString() })
	}

	def "comfirm page should have correct data listed" () {
		given: 'Create Autoforward wizard is open'
			createTestAutoforward()
			launchAutoforwardPopup()
		when: 'Message tab is open'
			message.displayed
			message.messageText.value('message to send')
			next.click()
			keyword.keywordText = 'Hello'
			next.click()
		then: 'Recipients tab should open'
			recipients.displayed
		when: 'a recipient is added'
			recipients.addRecipient('1234567890')
			recipients.addRecipient('generated-contact-1')
			recipients.addRecipient('generated-contact-2')
			next.click()
		then: 'Confirm tab should open'
			confirm.displayed
			confirm.keywordConfirm == 'HELLO'
			confirm.recipientCount == '3'
	}

	def launchAutoforwardPopup(String tab = ''){
		to PageMessageInbox
			bodyMenu.newActivity.click()
			waitFor { at CreateActivityDialog }
			autoforward.click()
			waitFor { at AutoforwardCreateDialog }
		if(tab == 'keyword'){
			message.messageText.value('message to send')
			next.click()
			waitFor { keyword.displayed}
		}
	}
}

