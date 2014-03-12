package frontlinesms2.services

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*

@TestFor(TextMessageInfoService)
class TextMessageInfoServiceSpec extends Specification {
	@Unroll
	def "should calculate the total number of characters and messages being sent"() {
		
		when:
			def messageInfo = service.getMessageInfos(text)
		then:
			messageInfo.charCount == chars
			messageInfo.remaining == remaining
			messageInfo.partCount == parts
		where:
			text      | chars | remaining | parts
			''        | 0     | 160       | 1
			'a' * 160 | 160   | 0         | 1
			'a' * 161 | 161   | 145       | 2
			'a' * 306 | 306   | 0         | 2
	}
}

