package frontlinesms2

import routing.CamelIntegrationSpec

class EmailToDispatcherRouteSpec extends CamelIntegrationSpec {
	String getFrom() {
		'seda:raw-email'
	}
	String getTo() {
		'seda:fmessages-to-process'
	}

	def "complete route test"() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: 'alice', dst: 'bob', content: 'subject'))
		when:
			// FIXME body here should be a message as provided by camel email component
			template.sendBodyAndHeaders('email body',
        		        [From: 'alice', To: 'bob', Subject: 'subject'])
		then:
       			resultEndpoint.assertIsSatisfied()
		        assert Fmessage.count() == 1
	}
}

