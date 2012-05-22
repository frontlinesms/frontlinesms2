package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*

@TestFor(ExpressionProcessorService)
class ExpressionProcessorServiceSpec extends Specification {

	@Unroll
	def "get expressions in the message content"() {
	expect:
		service.getExpressions(messageText) == expectedExpressions
	where:
		messageText 											| expectedExpressions
		'test message'                                          | []
		'please call us on ${contact_number}'                | ['${contact_number}']
		'sender name ${contact_name}, number ${contact_number}' | ['${contact_name}', '${contact_number}']
	}
}