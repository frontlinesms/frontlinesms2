package routing

import spock.lang.*
import grails.plugin.spock.*

import org.apache.camel.*
import org.apache.camel.builder.*
import org.apache.camel.impl.DefaultProducerTemplate

abstract class CamelIntegrationSpec extends IntegrationSpec {
	def camelContext

	def resultEndpoint
	def template

	def setup() {
		camelContext.addRoutes(createRouteBuilder())
		resultEndpoint = camelContext.getEndpoint('mock:result')
		template = new DefaultProducerTemplate(
			camelContext,
			camelContext.getEndpoint('direct:start'))
		template.start()
	}

	def cleanup() {
		template?.stop()
		println "Current routes:"
		for(r in camelContext.getRoutes()) {
			println "\t${r?.id} :: ${r}"
		}
		def testRoutes = [camelContext.getRouteDefinition('test-1'),
						camelContext.getRouteDefinition('test-2')]
		println "Removing routes: ${testRoutes}"
		camelContext.removeRouteDefinitions(testRoutes)
	}

	abstract String getFrom();
	abstract String getTo();

	private RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			public void configure() {
				from('direct:start').to(getFrom()).routeId('test-1')
				from(getTo()).to('mock:result').routeId('test-2')
			}
		}
	}
}
