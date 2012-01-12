package frontlinesms2.poll

import frontlinesms2.*
import frontlinesms2.radio.RadioShow

@Mixin(frontlinesms2.utils.GebUtil)
class RadioShowPollSpec extends geb.spock.GebReportingSpec {
	def "poll can be associated with a radioShow from the poll confirm screen"() {
		setup:
			def show = new RadioShow(name:"Morning Show").save(flush:true)
			assert !show.polls
		when:
			launchPollPopup('standard', "Will you send messages to this poll", false)
		then:
			waitFor { autoSortTab.displayed }
		when:
			goToTab(7)
		then:
			waitFor { confirmationTab.displayed }
			$(".radio-show-select").displayed
		when:
			$(".radio-show-select").value(show.id)
			pollForm.title = "Morning Show Poll"
			done.click()
			show.refresh()
		then:
			waitFor { $(".summary").displayed }
			show.polls.size() == 1
	}
	
	def "poll can be associated to radioshow from the 'More Actions' dropdown"() {
		setup:
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
			def show = new RadioShow(name:"Morning Show").save(flush:true)
		when:
			go "message/poll/${poll.id}"
		then:
			waitFor { title == "Poll" }
		when:
			$("#poll-button-list").find("li:nth-child(3) select").jquery.val("radioShow")
			$("#poll-button-list").find("li:nth-child(3) select").jquery.trigger("change")
		then:
			waitFor { $("#ui-dialog-title-modalBox").displayed }
		when:
			$(".radio-show-select").value(show.id)
			$("#done").click()
			show.refresh()
		then:
			show.polls.size() == 1			
	}
	
	def launchPollPopup(pollType='standard', question='question', enableMessage=true) {
		go 'message'
		$("#create-activity a").click()
		waitFor { $("#ui-dialog-title-modalBox").displayed }
		$("input", class: "poll").click()
		$("#submit").click()
		waitFor { at PagePollCreate }
		pollForm.'poll-type' = pollType
		if(question) pollForm.question = question
		pollForm."dontSendMessage" = !enableMessage
		next.click()
	}
	
	def goToTab(tab) {
		$(".tabs-$tab").click()
	}
	
}