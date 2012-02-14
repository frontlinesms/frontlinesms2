package frontlinesms2.poll

import frontlinesms2.*
import frontlinesms2.radio.RadioShow

@Mixin(frontlinesms2.utils.GebUtil)
class RadioShowPollSpec extends geb.spock.GebReportingSpec {
	def "poll can be associated with a radioShow from the poll walk through"() {
		setup:
			def show = new RadioShow(name:"Morning Show").save(flush:true)
			assert !show.polls
		when:
			launchPollPopup('standard', "Will you send messages to this poll", false)
			$(".radio-show-select").displayed
			$(".radio-show-select").value(show.id)
			next.click()
		then:
			waitFor { autoSortTab.displayed }
		when:
			goToTab(7)
		then:
			waitFor { confirmationTab.displayed }
		when:
			pollForm.name = "Morning Show Poll"
			done.click()
			show.refresh()
		then:
			waitFor { $(".summary").displayed }
			show.polls.size() == 1
	}
	
	def "poll can be associated to radioshow from the 'More Actions' dropdown"() {
		setup:
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
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
	
	def "polls associated to a radio show are listed below the currently viewed show"() {
		setup:
			def poll = Poll.createPoll(name: 'Who is badder', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question", autoReplyText: "Thanks").save(failOnError:true, flush:true)
			def poll2 = Poll.createPoll(name: 'Who will win?', choiceA:'Uhuru Kenyatta', choiceB:'Fred Ruto', question: "politics").save(failOnError:true, flush:true)
			def show = new RadioShow(name:"Morning Show").save(flush:true)
			show.addToPolls(poll)
			show.save(flush:true)
		when:
			go "message/radioShow/${show.id}"
		then:
			waitFor { title == "RadioShow" }
			$("#shows-submenu").find("#radio-show-polls")*.text() == ["Who is badder poll"]
			!$("#activities-submenu").text().contains("Who is badder poll")
	}
	
	def launchPollPopup(pollType='standard', question='question', enableMessage=true) {
		go 'message'
		$("#create-activity a").click()
		waitFor { $("#ui-dialog-title-modalBox").displayed }
		$("input", class: "poll").click()
		$("#submit").click()
		waitFor(10) { $("#ui-dialog-title-modalBox").text()?.equalsIgnoreCase("New poll") }
		at PagePollCreate
		pollForm.'poll-type' = pollType
		if(question) pollForm.question = question
		pollForm."dontSendMessage" = !enableMessage
	}
	
	def goToTab(tab) {
		$(".tabs-$tab").click()
	}
	
}
