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
			configure.steps[0].jquery.find("#messageText").value("Sample Text")
			configure.steps[1].jquery.find("#joinGroup").value(1)
			next.click()
			confirm.name.value("do it all")
		then:
			//check if confirm data is okay
			submit.click()
	}

	def 'can edit an existing custom activity'() {

	}

}