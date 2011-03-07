package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

import org.apache.camel.*
import org.apache.camel.builder.*
import org.apache.camel.impl.DefaultProducerTemplate

class EmailToDispatcherRouteSpec extends UnitSpec {
	def camelContext

	def resultEndpoint
	def template

	def "complete route test"() {
		given:
			setupContext()
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

	private void setupContext() {
			camelContext.addRoutes(createRouteBuilder())
			resultEndpoint = camelContext.getEndpoint('mock:result')
			template = new DefaultProducerTemplate(
				camelContext,
				camelContext.getEndpoint('direct:start'))
			template.start()
	}

	private RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			public void configure() {
				from('direct:start').to('seda:raw-email')
				from('seda:fmessages-to-process').to('mock:result')
			}
		}
	}
}

