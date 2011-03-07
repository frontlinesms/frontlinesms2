package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

import org.apache.camel.*
import org.apache.camel.builder.*
import org.apache.camel.impl.DefaultProducerTemplate

class MessageStorageRouteSpec extends UnitSpec {
	def camelContext

	def resultEndpoint
	def template

	def "test storage"() {
		given:
			def fmessage = new Fmessage(src: 'alice', dst: 'bob', content: 'subject')
		        assert Fmessage.count() == 0
			setupContext()
			resultEndpoint.expectedBodiesReceived(fmessage)
		when:
			template.sendBodyAndHeaders(fmessage, [:])
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
				from('direct:start').to('seda:fmessages-to-store')
				from('seda:fmessages-to-process').to('mock:result')
			}
		}
	}
}

