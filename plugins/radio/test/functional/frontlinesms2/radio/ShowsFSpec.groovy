package frontlinesms2.radio

import java.text.SimpleDateFormat;
import java.util.Date

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
class ShowsFSpec extends RadioBaseSpec {
	def "should be able to create new shows"() {
		when:
			go "message"
			$("a", text: "Create new show").click()
			waitFor { $("#modalBox").displayed }
			$("input", name: 'name').value("show name")
			$("#done").click()
			waitFor { $("a", text:"show name").displayed }
		then:
	       	$("a", text:"show name").displayed
	}

	def "should throw validation errors when name is not given for a show"() {
		when:
			go "message"
			$("a", text: "Create new show").click()
			waitFor { $("#modalBox").displayed }
			$("input", name: 'name').value("")
			$("#done").click()
		then:
			println "flash message:" + $("div.flash").text()
			waitFor { $("div.flash").text().contains("Name is not valid") }
	}
	
	def "separator is displayed for radio messages from different days"() {
		given:
			def show = new RadioShow(name: "Test")
			 def messageA = new Fmessage(src: '+3245678', dst: '+123456789', text: "What is diabetes?", dateReceived: new Date() - 2).save(failOnError: true)
			 def messageB = new Fmessage(src: 'Jill', dst: '+254115533', text: "I love life", dateReceived: new Date() - 1).save(failOnError: true)
			 show.addToMessages(messageA)
			 show.addToMessages(messageB)
			 show.save(failOnError: true, flush: true)
		when:
			go "message/radioShow/${show.id}"
		then:
			getColumnText('message-list', 0)[1] == "${dateToString(new Date()-2)}"
	}
	
	String dateToString(Date date) {
		new SimpleDateFormat("EEEE, MMMM dd", Locale.US).format(date)
	}
}

