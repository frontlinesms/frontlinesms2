package frontlinesms2

import routing.CamelIntegrationSpec

class MessageStorageRouteSpec extends CamelIntegrationSpec {
	def "test storage"() {
		given:
			def fmessage = new Fmessage(src: 'alice', dst: 'bob', content: 'subject')
		        assert Fmessage.count() == 0
			resultEndpoint.expectedBodiesReceived(fmessage)
		when:
			template.sendBodyAndHeaders(fmessage, [:])
		then:
       			resultEndpoint.assertIsSatisfied()
		        assert Fmessage.count() == 1
	}

	@Override
	String getFrom() {
		'seda:fmessages-to-store'
	}

	@Override
	String getTo() {
		'seda:fmessages-to-process'
	}
}

