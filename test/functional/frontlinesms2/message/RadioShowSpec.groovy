package frontlinesms2.message

import frontlinesms2.RadioShow
import frontlinesms2.Fmessage


class RadioShowSpec extends grails.plugin.geb.GebSpec {
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

	def "should be able to list all the messages for a show"() {
		setup:
			def showInstance = new RadioShow(name: "show1")
			showInstance.addToMessages(new Fmessage(text: "hello1"))
			showInstance.addToMessages(new Fmessage(text: "hello2"))
			showInstance.save(flush: true)
		when:
			assert RadioShow.count() == 1
			go "message"
			$("a", text: "show1").click()
			waitFor { $("a", text:"hello1").displayed }
		then:
			$("#messages tbody tr").size() == 2
			$("#messages tbody tr").collect{ it.find("td:nth-child(4)")}*.text().containsAll("hello1", "hello2")
	}

	def "should throw validation errors when name is not given for a show"() {
		when:
			go "message"
			$("a", text: "Create new show").click()
			waitFor { $("#modalBox").displayed }
			$("input", name: 'name').value("")
			$("#done").click()
			waitFor { $("div.flash", text:"Name is not valid").displayed }
		then:
			$("div.flash", text:"Name is not valid").displayed
	}
}

