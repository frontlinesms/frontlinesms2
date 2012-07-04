import grails.test.mixin.*
import spock.lang.*

import frontlinesms2.*

@TestFor(CoreUrlMappings)
@Mock(SearchController)
class SearchUrlMappingITests extends Specification {

	def testSearchMapping() {
		assertForwardUrlMapping('/search/result/show/123', controller:'search', action:'result') {
			messageId = 123
		}
	}
}
