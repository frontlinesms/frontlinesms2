package frontlinesms2.controller

import frontlinesms2.*

class AutoreplyControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def setup() {
		controller = new AutoreplyController()
	}

	def "can create an Autoreply with a keyword"() {
		when:
			controller.params.name = "Color"
			controller.params.keyword = "color"
			controller.params.autoreplyText = "ahhhhhhhhh"
			controller.save()
			def autoreply = Autoreply.findByName("Color")
			def keyword = Keyword.findByValue("COLOR")
		then:
			println Keyword.list()
			keyword != null
			autoreply != null
			autoreply.keyword == keyword
			autoreply.autoreplyText == "ahhhhhhhhh"
	}
	
	def "can create an Autoreply will blank keyword value"() {
		when:
			controller.params.name = "Thanks"
			controller.params.keyword = ""
			controller.params.autoreplyText = "Thank you for the text"
			controller.save()
		then:
			def autoreply = Autoreply.findByName("Thanks")
			autoreply.autoreplyText == "Thank you for the text"
			autoreply.keyword.value == ''
	}
	
	def "can edit an Autoreply"() {
		when:
			def keyword = new Keyword(value:"color")
			def autoreply = new Autoreply(name: "Color", keyword: keyword, autoreplyText:"ahhhhhhhhh")
			autoreply.save(flush: true, failOnError: true)
			controller.params.ownerId = autoreply.id
			controller.params.name = "ColorZ"
			controller.params.keyword = "colorz"
			controller.params.autoreplyText = "blue, i mean green"
			controller.save()
			autoreply.refresh()
			keyword.refresh()
		then:
			keyword != null
			autoreply != null
			Keyword.findByValue("color") == null
			Autoreply.findByName("Color") == null
			keyword.value == "COLORZ"
			autoreply.name == "ColorZ"
			autoreply.keyword == keyword
			autoreply.autoreplyText == "blue, i mean green"
	}
}
