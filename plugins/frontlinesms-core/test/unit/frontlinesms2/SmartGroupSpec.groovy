package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(SmartGroup)
class SmartGroupSpec extends Specification {
	@Unroll
	def 'a SmartGroup must have a name and at least one search parameter'() {
		expect:
			new SmartGroup(name:name, contactName:contactName).validate() == valid
		where:
			valid | name                     | contactName
			false | null                     | null
			false | 'people who like people' | null
			false | null                     | 'bob'
			true  | 'people who like people' | 'bob'
	}
}

