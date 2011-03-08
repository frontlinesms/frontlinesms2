package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

import org.apache.camel.*
import org.apache.camel.builder.*
import org.apache.camel.impl.DefaultProducerTemplate

class EmailTranslationRouteSpec extends IntegrationSpec {
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

	def "test translation route"() {
		given:
			resultEndpoint.expectedBodiesReceived(
					new Fmessage(src: 'alice', dst: 'bob', content: 'subject'))
		when:
			// FIXME body here should be a message as provided by camel email component
			template.sendBodyAndHeaders('email body',
        		        [From: 'alice', To: 'bob', Subject: 'subject'])
		then:
       			resultEndpoint.assertIsSatisfied()
			assert Fmessage.count() == 0		
	}

	private RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			public void configure() {
				from('direct:start').to('seda:raw-email').routeId('test-1')
				from('seda:fmessages-to-store').to('mock:result').routeId('test-2')
			}
		}
	}
}

