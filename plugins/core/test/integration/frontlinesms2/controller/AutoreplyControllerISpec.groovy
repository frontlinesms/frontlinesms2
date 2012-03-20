package frontlinesms2.controller

import frontlinesms2.*

class AutoreplyControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def setup() {
		controller = new AutoreplyController()
	}

	def "can save an Autoreply"() {
		when:
			controller.params.name = "Color"
			controller.params.keyword = "color"
			controller.params.autoReplyText = "ahhhhhhhhh"
			controller.save()
			def autoreply = Autoreply.findByName("Color")
			def keyword = Keyword.findByValue("COLOR")
		then:
			keyword != null
			autoreply != null
			autoreply.keyword == keyword
			autoreply.sentMessageText == "ahhhhhhhhh"
	}
	
	def "can edit an Autoreply"() {
		when:
			def keyword = new Keyword(value:"color")
			def autoreply = new Autoreply(name: "Color", keyword: keyword, sentMessageText:"ahhhhhhhhh")
			autoreply.save(flush: true, failOnError: true)
			controller.params.ownerId = autoreply.id
			controller.params.name = "ColorZ"
			controller.params.keyword = "colorz"
			controller.params.autoReplyText = "blue, i mean green"
			controller.edit()
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
			autoreply.sentMessageText == "blue, i mean green"
	}
}
