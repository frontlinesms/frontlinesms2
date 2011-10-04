package frontlinesms2

class SearchUrlMappingITests extends grails.test.GrailsUrlMappingsTestCase {
	void testSearchMappping() {
		assertForwardUrlMapping('/search/result/123', controller:'search', action:'result') {
			messageId = 123
		}
	}
}
