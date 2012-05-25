package net.frontlinesms.install4j.custom

import spock.lang.*

class UrlSpec extends Specification {
	def "Test for the email validation"() {
		given:
			URLtest t = new URLtest()
		expect:
			t.testEmail(text) == value
		where:
			text			| value
			'test@gmail.com'| true
			'test.com' 		| false
			'@home.com' 	| false
			'@.com' 		| false
			'@.' 			| false
			'.com' 			| false
	}

	def "Test for web address valition"() {
		given:
			URLtest t = new URLtest()
		expect:
			t.test(text) == value
		where:
			text				| value
			'www.home.com' 		| true
			'test.home.com'		| false
			'http://test.com' 	| true
			'http:/test.com' 	| false
			'http:test.com' 	| false
			'gov://test.com' 	| false
			'gov:/test.com' 	| false
			'gov:test.com' 		| false
			'home.com' 			| false
			'.com' 				| false
			'..' 				| false
			'http.com' 			| false
			'www.home.com.co' 	| true
			'www.home.co.ke.hu' | true
	}
}
