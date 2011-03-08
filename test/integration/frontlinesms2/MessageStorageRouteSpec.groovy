package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

import org.apache.camel.*
import org.apache.camel.builder.*
import org.apache.camel.impl.DefaultProducerTemplate

class MessageStorageRouteSpec extends IntegrationSpec {
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

	private RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			public void configure() {
				from('direct:start').to('seda:fmessages-to-store').routeId('test-1')
				from('seda:fmessages-to-process').to('mock:result').routeId('test-2')
			}
		}
	}
}

