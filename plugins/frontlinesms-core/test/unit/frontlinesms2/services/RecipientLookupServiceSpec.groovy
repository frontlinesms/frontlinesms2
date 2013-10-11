package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*

@Mock([Contact, SmartGroup, Group])
@TestFor(RecipientLookupService)
class RecipientLookupServiceSpec extends Specification {
	def recipients

	def setup() {
		recipients = [
			'contact-1', 'contact-102', 'contact-33',
			'group-23', 'group-3',
			'smartgroup-68', 'smartgroup-72', 'smartgroup-200', 'smartgroup-2468',
			'address-+24567890'
		]
	}

	def "getContacts() invokes Contact.get for each occurence of contact-* in recipient list"() {
		setup:
			def invocationCount = 0
			Contact.metaClass.static.get = { String id ->
				invocationCount++
			}
		when:
			service.getContacts(recipients)
		then:
			invocationCount == 3
	}

	def "getGroups() invokes Group.get for each occurence of group-* in recipient list"() {
		setup:
			def invocationCount = 0
			Group.metaClass.static.get = { String id ->
				invocationCount++
			}
		when:
			service.getGroups(recipients)
		then:
			invocationCount == 2
	}

	def "getSmartGroups() invokes SmartGroup.get for each occurence of smartgroup-* in recipient list"() {
		setup:
			def invocationCount = 0
			SmartGroup.metaClass.static.get = { String id ->
				invocationCount++
			}
		when:
			service.getSmartGroups(recipients)
		then:
			invocationCount == 4
	}

	def "getManualAddresses() invokes stripPrefix for each occurence of address-* in the recipient list"() {
		setup:
			def invocationCount = 0
			service.metaClass.stripPrefix = { String it -> invocationCount++ }
		when:
			service.getManualAddresses(recipients)
		then:
			invocationCount == 1
	}
}
