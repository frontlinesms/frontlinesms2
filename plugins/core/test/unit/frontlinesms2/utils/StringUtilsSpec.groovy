package frontlinesms2.utils

import spock.lang.*
import grails.plugin.spock.*

class StringUtilsSpec extends UnitSpec {
	def 'it should unaccent characters'() {
		when:
			true
		then:
			StringUtils.unAccent("a, â, à, A, Â, À") == "a, a, a, A, A, A"
	}
}