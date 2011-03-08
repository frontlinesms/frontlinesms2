package frontlinesms2

import routing.CamelIntegrationSpec

class SimpleRouteSpec extends CamelIntegrationSpec {
	def "test simple route"() {
		given:
			resultEndpoint.expectedBodiesReceived("hello")
		when:
			template.sendBodyAndHeaders("hello", [:])
		then:
       			resultEndpoint.assertIsSatisfied()
	}

	@Override
	String getFrom() {
		'seda:simple'
	}

	@Override
	String getTo() {
		'seda:simple'
	}
}

