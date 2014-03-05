import spock.lang.*

import frontlinesms2.*

@TestFor(CoreUrlMappings)
@Mock(SearchController)
class SearchUrlMappingSpec extends Specification {
	def testSearchMapping() {
		expect:
			assertForwardUrlMapping('/search/result/show/123', controller:'search', action:'result') {
				interactionId = 123
			}
	}
}
