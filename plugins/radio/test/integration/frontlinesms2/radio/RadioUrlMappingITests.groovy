package frontlinesms2

class MessageAndActivityUrlMappingITests extends grails.test.GrailsUrlMappingsTestCase {

	def testRadioShowView() {
		assertForwardUrlMapping('/message/radioShow/123', controller:'message', action:'radioShow') {
			ownerId = 123
		}

	}

	def testRadioMessageView() {
		assertForwardUrlMapping('/message/radioShow/123/show/3', controller:'message', action:'radioShow') {
			ownerId = 123
			messageId = 3
		}
	}
}

