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
			waitFor { at CreateActivityDialog}
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
			waitFor { at CreateActivityDialog}
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
		//TODO ensure that group has to be selected
	}

	def 'can add and remove steps in the confiure tab'(){
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
			configure.stepButton("reply").click()
			configure.stepButton("join").click()
		then:
			configure.steps.size() == 2
		when:
			configure.steps[0].jquery.find(".remove-step").click()
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
			waitFor { at CreateActivityDialog}
		when:
			customactivity.click()
		then:
			waitFor('slow') { at CustomActivityCreateDialog }
		when:
			keyword.keywordText.value("test")
			next.click()
			configure.stepButton("reply").click()
			configure.stepButton("join").click()
			configure.steps[0].jquery.find("#autoreplyText").value("Sample Text")
			configure.steps[1].jquery.find("#group").value(Group.findByName("Camping").id)
			next.click()
			confirm.name.value("Wewe wacha hakuna haja")
		then:
			confirm.stepActionsConfirm.contains("Camping")
			confirm.stepActionsConfirm.contains("Sample Text")
			submit.click()
			waitFor("very slow") { summary.displayed }
			cancel.click()
		when:
			to PageMessageCustomActivity, CustomActivity.findByName("Wewe wacha hakuna haja")
		then:
			waitFor { title?.toLowerCase().contains("custom activity") }
			def activity = CustomActivity.findByName("Wewe wacha hakuna haja")
			activity.name == "Wewe wacha hakuna haja"
			activity.steps.size() == 2
	}

	def 'can edit an existing custom activity'() {
		given:
			createTestGroups()
			createTestCustomActivities()
		when:
			to PageMessageCustomActivity, CustomActivity.findByName("Do it all")
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
			configure.stepButton("reply").click()
			configure.stepButton("join").click()
			configure.steps[2].jquery.find("#autoreplyText").value("Sample Text")
			configure.steps[3].jquery.find("#group").value(Group.findByName("Camping").id)
			next.click()
			confirm.name.value("ni hivyo hivyo tu")
		then:
			confirm.stepActionsConfirm.contains("Camping")
			confirm.stepActionsConfirm.contains("Sample Text")
			submit.click()
			waitFor("very slow") { summary.displayed }
			cancel.click()
		when:
			to PageMessageCustomActivity, CustomActivity.findByName("ni hivyo hivyo tu")
		then:
			waitFor { title?.toLowerCase().contains("custom activity") }
			def activity = CustomActivity.findByName("ni hivyo hivyo tu")
			activity.name == "ni hivyo hivyo tu"
			activity.steps.size() == 4
	}

}