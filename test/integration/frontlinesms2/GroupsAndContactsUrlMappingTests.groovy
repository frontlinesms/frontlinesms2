package frontlinesms2

class GroupsAndContactsUrlMappingTests extends grails.test.GrailsUrlMappingsTestCase {
	void testContactMappping() {
		assertForwardUrlMapping('/contact/show/1', controller:'contact', action:'show') {
			contactId = 1
			groupId = null
		}
	}

	void testGroupMapping() {
		assertForwardUrlMapping('/group/show/123', controller:'contact', action:'list') {
			groupId = 123
			contactId = null
		}
	}

	void testGroupAndContactMapping() {
		assertForwardUrlMapping('/group/show/1/contact/show/2', controller:'contact', action:'show') {
			contactId = 2
			groupId = 1
		}
	}
}
