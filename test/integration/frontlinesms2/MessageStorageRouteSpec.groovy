package frontlinesms2

import routing.CamelIntegrationSpec

class MessageStorageRouteSpec extends CamelIntegrationSpec {
	def "test storage"() {
		given:

		// TODO Work around for apparent non-transactional nature of Spock integration specs
			Fmessage.findAll().each() {
				it.delete()
			}
			assert Fmessage.count() == 0

			def fmessage = new Fmessage(src: 'alice', dst: 'bob', content: 'subject')
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

