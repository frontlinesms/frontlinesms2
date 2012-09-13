package net.frontlinesms.install4j.custom

import spock.lang.*

class FutilUnitSpec extends Specification {
	def "Test for the email validation"() {
		expect:
			Futil.validateEmailAddress(text) == valid
		where:
			text             | valid
			'test@gmail.com' | true
			'test.com'       | false
			'@home.com'      | false
			'@.com'          | false
			'@.'             | false
			'.com'           | false
	}

	def "Test for web address valition"() {
		expect:
			Futil.validateUrl(text) == valid
		where:
			text                | valid
			'www.home.com'      | true
			'test.home.com'     | false
			'http://test.com'   | true
			'http:/test.com'    | false
			'http:test.com'     | false
			'gov://test.com'    | false
			'gov:/test.com'     | false
			'gov:test.com'      | false
			'home.com'          | false
			'.com'              | false
			'..'                | false
			'http.com'          | false
			'www.home.com.co'   | true
			'www.home.co.ke.hu' | true
	}
}

