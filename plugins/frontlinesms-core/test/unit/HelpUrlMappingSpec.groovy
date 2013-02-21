import spock.lang.*

import frontlinesms2.*

@TestFor(CoreUrlMappings)
@Mock([HelpController])
class HelpUrlMappingSpec extends Specification {
	def 'updateShowNewFeatures should map directly'() {
		expect:
		assertForwardUrlMapping('/help/updateShowNewFeatures', controller:'help', action:'updateShowNewFeatures')
	}

	def 'newfeatures should map directly'() {
		expect:
		assertForwardUrlMapping('/help/newfeatures', controller:'help', action:'newfeatures')
	}
}

