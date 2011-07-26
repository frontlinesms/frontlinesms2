package frontlinesms2

import grails.plugin.spock.UnitSpec

class ShowSpec extends UnitSpec {

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
}