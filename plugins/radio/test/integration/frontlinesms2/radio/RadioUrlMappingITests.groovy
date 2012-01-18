package frontlinesms2.radio

class RadioUrlMappingITests extends grails.test.GrailsUrlMappingsTestCase {

	def testRadioShowView() {
		assertForwardUrlMapping('/message/radioShow/123', controller:'radioShow', action:'radioShow') {
			ownerId = 123
		}

	}

	def testRadioMessageView() {
		assertForwardUrlMapping('/message/radioShow/123/show/3', controller:'radioShow', action:'radioShow') {
			ownerId = 123
			messageId = 3
		}
	}
}
