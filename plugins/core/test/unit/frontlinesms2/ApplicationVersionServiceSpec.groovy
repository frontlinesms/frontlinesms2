package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ApplicationVersionServiceSpec extends UnitSpec {
	private def s = new ApplicationVersionService()
	
	def 'check get newest'() {
		expect:
			'1.1' == s.getNewest('1.0', '1.1')
			'1.2' == s.getNewest('1.0', '1.2', '1.1')
			'1.0.1' == s.getNewest('1.0', '1.0.1')
			'1.1' == s.getNewest('1.0', '1.0.1', '1.1')
	}
}

