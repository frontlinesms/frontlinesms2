package frontlinesms2.radio

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*

@TestFor(RadioShow)
class RadioShowSpec extends Specification {

	def "should check for validations"() {
		when:
			def invalid1 = new RadioShow(name: null)
			def invalid2 = new RadioShow(name: '')
		then:
			!invalid1.validate()
			!invalid2.validate()
	}

	def "shows names should be unique"() {
		new RadioShow(name: 'show1').save()
		when:
			def invalidShow1 = new RadioShow(name: "show1")
			def invalidShow2 = new RadioShow(name: "SHOW1")
			def validShow = new RadioShow(name: "show2")
		then:
			!invalidShow1.validate()
			!invalidShow2.validate()
			validShow.validate()
	}
}
