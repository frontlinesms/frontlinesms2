package frontlinesms2.customactivity

import frontlinesms2.*
import frontlinesms2.message.PageMessageInbox
import frontlinesms2.popup.*

class CustomActivityCedSpec extends CustomActivityBaseSpec {
	def "can launch custom activity screen from create new activity link" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			customactivity.click()
		then:
			waitFor('slow') { at CustomActivityCreateDialog }
	}

	def 'validation in keyword tab works'() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			customactivity.click()
		then:
			waitFor('slow') { at CustomActivityCreateDialog }
		when:
			next.click()
		then:
			error.displayed
	}

	def 'validation in configure tab works'() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			customactivity.click()
		then:
			waitFor('slow') { at CustomActivityCreateDialog }
		when:
			keyword.keywordText.value("test")
			next.click()
			configure.stepActions.jquery.val("join").jquery.trigger('change')
			next.click()
		then:
			error.displayed	
			errorText == "please select a group"
	}

	def 'can add and remove steps in the confiure tab'(){
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			customactivity.click()
		then:
			waitFor('slow') { at CustomActivityCreateDialog }
		when:
			keyword.keywordText.value("test")
			next.click()
			configure.stepActions.jquery.val("reply").jquery.trigger('change')
			configure.stepActions.jquery.val("join").jquery.trigger('change')
		then:
			configure.steps.size() == 2
		when:
			configure.steps[0].jquery.find(".remove-command").click()
		then:
			waitFor { configure.steps.size() == 1 }
	}

	def 'can create a new custom activity'() {
		given:
			createTestGroups()
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			customactivity.click()
		then:
			waitFor('slow') { at CustomActivityCreateDialog }
		when:
			keyword.keywordText.value("test")
			next.click()
			configure.stepActions.jquery.val("reply").jquery.trigger('change')
			configure.stepActions.jquery.val("join").jquery.trigger('change')
			configure.steps[0].jquery.find("textarea[name=autoreplyText]").value("Sample Text")
			configure.steps[1].jquery.find("#group").value(remote { Group.findByName("Camping").id })
			next.click()
			confirm.name.value("Wewe wacha hakuna haja")
		then:
			confirm.stepActionsConfirm.contains("Camping")
			confirm.stepActionsConfirm.contains("Sample Text")
			submit.click()
			waitFor("very slow") { summary.displayed }
	}

	def 'can edit an existing custom activity'() {
		given:
			createTestGroups()
			createTestCustomActivities()
		when:
			to PageMessageCustomActivity, remote { CustomActivity.findByName("Do it all").id }
		then:
			waitFor { title?.toLowerCase().contains("custom activity") }
			moreActions.value("edit").jquery.click()
			waitFor { at CustomActivityCreateDialog }
			keyword.keywordText == "CUSTOM"
		when:
			keyword.keywordText.value("test")
			next.click()
		then:
			configure.steps.size() == 2
		when:
			configure.stepActions.jquery.val("reply").jquery.trigger('change')
			configure.stepActions.jquery.val("join").jquery.trigger('change')
			configure.steps[2].jquery.find("textarea[name=autoreplyText]").value("Sample Text")
			configure.steps[3].jquery.find("#group").value(remote { Group.findByName("Camping").id })
			next.click()
			confirm.name.value("ni hivyo hivyo tu")
		then:
			confirm.stepActionsConfirm.contains("Camping")
			confirm.stepActionsConfirm.contains("Sample Text")
			submit.click()
			waitFor("very slow") { summary.displayed }
	}

	def 'Foward action step is part of the options present for the Customactivity'() {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog}
		when:
			customactivity.click()
		then:
			waitFor('slow') { at CustomActivityCreateDialog }
		when:
			keyword.keywordText.value("test")
			next.click()
		then:
			configure.forwardButton.present
	}

}

