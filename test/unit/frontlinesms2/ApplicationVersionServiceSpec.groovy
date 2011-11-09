package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ApplicationVersionServiceSpec extends UnitSpec {
	private def s = new ApplicationVersionService()
	
	def 'check get newest'() {
		expect:
			assertNewest '1.1', ['1.0', '1.1']
			assertNewest '1.2', ['1.0', '1.2', '1.1']
			assertNewest '1.0.1', ['1.0', '1.0.1']
			assertNewest '1.1', ['1.0', '1.0.1', '1.1']
	}
	
	private def assertNewest(expected, options) {
		expected == s.getNewest(options)
	}
}

