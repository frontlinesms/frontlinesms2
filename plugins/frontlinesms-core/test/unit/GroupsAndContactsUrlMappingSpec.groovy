import grails.test.mixin.*
import spock.lang.*

import frontlinesms2.*

@TestFor(CoreUrlMappings)
@Mock([ContactController])
class GroupsAndContactsUrlMappingSpec extends Specification {
	def testNullContactMapping() {
		expect:
			assertForwardUrlMapping('/contact/show', controller:'contact', action:'show')
	}
	
	def testContactMappping() {
		expect:
			assertForwardUrlMapping('/contact/show/1', controller:'contact', action:'show') {
				contactId = 1
			}
	}

	def testGroupAndContactMapping() {
		expect:
			assertForwardUrlMapping('/group/show/1/contact/show/2', controller:'contact', action:'show') {
				contactId = 2
				groupId = 1
			}
	}
}

