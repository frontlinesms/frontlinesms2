package frontlinesms2.radio

import grails.test.mixin.*
import spock.lang.*

@TestFor(RadioShowController)
@Mock(RadioShow)

class RadioShowControllerSpec extends Specification {

	def "should create a show"() {
		setup:
			params.name = "show name"
		when:
			controller.save()
		then:
			RadioShow.findByName("show name")
			controller.response.redirectUrl == "/message/inbox"
	}

	def "save should throw error when validation fails"() {
		setup:
			params.name = ""
		when:
			controller.save()
		then:
			controller.flash.message == "radio.show.invalid.name"
			controller.response.redirectUrl == "/message/inbox"
	}

	def "can rename a radioshow folder"() {
		setup:
			def radioshow = new RadioShow(name:"Name 1").save()
		when:
			params.name = "Name 2"
			params.ownerId = radioshow.id
			controller.save()
		then:
			radioshow.name == "Name 2"
	}
}
