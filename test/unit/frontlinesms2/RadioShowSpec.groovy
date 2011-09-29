package frontlinesms2

import grails.plugin.spock.UnitSpec

class RadioShowSpec extends UnitSpec {

	def "should check for validations"() {
		setup:
			mockDomain(RadioShow)
		when:
			def invalid1 = new RadioShow(name: null)
			def invalid2 = new RadioShow(name: '')
		then:
			!invalid1.validate()
			!invalid2.validate()
	}

	def "shows names should be unique"() {
		mockDomain(RadioShow, [new RadioShow(name: 'show1')])
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
