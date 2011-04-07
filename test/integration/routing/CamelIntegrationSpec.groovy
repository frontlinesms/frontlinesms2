package routing

import spock.lang.*
import grails.plugin.spock.*

import org.apache.camel.*
import org.apache.camel.builder.*
import org.apache.camel.impl.DefaultProducerTemplate
import org.apache.camel.component.mock.MockEndpoint
import frontlinesms2.Fmessage

abstract class CamelIntegrationSpec extends IntegrationSpec {
	// This is the CamelContext used by Grails.  It will be injected for you.
	def camelContext

	// This is the endpoint of the route to be tested
	def resultEndpoint
	// This is the start of the route to be tested
	def template

	def setup() {
		camelContext.addRoutes(createRouteBuilder())
		resultEndpoint = camelContext.getEndpoint('mock:result')
		template = new DefaultProducerTemplate(
			camelContext,
			camelContext.getEndpoint('direct:start'))
//		template = createProducerTemplate()
		template.start()
	}

	def cleanup() {
		template?.stop()
		MockEndpoint.resetMocks(camelContext)
		def testRoutes = [camelContext.getRouteDefinition('test-1'),
						camelContext.getRouteDefinition('test-2')]
		camelContext.removeRouteDefinitions(testRoutes)

		// TODO Work around for apparent non-transactional nature of Spock integration specs
		Fmessage.findAll().each() {
			it.delete()
		}
	}

	abstract String getFrom();
	abstract String getTo();

	ProducerTemplate createProducerTemplate() {
		new DefaultProducerTemplate(
				camelContext,
				camelContext.getEndpoint('direct:start'))
	}

	private RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			public void configure() {
				from('direct:start').to(getFrom()).routeId('test-1')
				from(getTo()).to('mock:result').routeId('test-2')
			}
		}
	}
}
