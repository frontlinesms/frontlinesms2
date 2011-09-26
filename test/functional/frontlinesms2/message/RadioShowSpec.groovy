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

