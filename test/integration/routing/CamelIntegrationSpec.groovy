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
		template = createProducerTemplate()
		template.start()
	}

	def cleanup() {
		template?.stop()
		MockEndpoint.resetMocks(camelContext)
		def testRoutes = [camelContext.getRouteDefinition('test-from'),
						camelContext.getRouteDefinition('test-to')]
		camelContext.removeRouteDefinitions(testRoutes - null)

		// TODO Work around for apparent non-transactional nature of Spock integration specs // this really shouldn't be necessary... possibly instead to do with asynchronous nature of route processing?
		Fmessage.findAll().each {
			it.delete()
		}
	}

	abstract String getFrom();
	abstract String getTo();

	ProducerTemplate createProducerTemplate() {
		new DefaultProducerTemplate(camelContext,
				camelContext.getEndpoint('direct:start'))
	}

	private RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			public void configure() {
				def from = getFrom()
				if(from) from('direct:start').to(from).routeId('test-from')
				
				def to = getTo()
				if(to) from(to).to('mock:result').routeId('test-to')
			}
		}
	}
}
