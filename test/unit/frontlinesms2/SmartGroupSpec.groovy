package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class SmartGroupSpec extends UnitSpec {
	def 'a SmartGroup must have a name and at least one search parameter'() {
		given:
			mockForConstraintsTests(SmartGroup)
		when:
			def noName = new SmartGroup()
			def withName = new SmartGroup(name:'people who like people')
			def withNameAndContactName = new SmartGroup(name:'people who like people', contactName:'bob')
		then:
			!noName.validate()
			noName.errors.name
		and:	
			!withName.validate()
			!withName.errors.name
		and:
			withNameAndContactName.validate()
	}
}

