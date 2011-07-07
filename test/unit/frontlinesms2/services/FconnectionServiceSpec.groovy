package frontlinesms2.services

import spock.lang.*
import grails.plugin.spock.*
import org.apache.camel.CamelContext
import frontlinesms2.FconnectionService
import frontlinesms2.Fconnection
import frontlinesms2.RouteStatus

class FconnectionServiceSpec extends UnitSpec  {
	def 'Unconnected Fconnection gives a status of NOT_CONNECTED'() {
		given:
			def service = new FconnectionService()
			service.camelContext = Mock(CamelContext)
			def notConnected = Mock(Fconnection)
		when:
			def status = service.getRouteStatus(notConnected)
		then:
			status == RouteStatus.NOT_CONNECTED
	}
	
	def 'Connected Fconnection gives a status of CONNECTED'() {
		given:
			def service = new FconnectionService()
			def context = Mock(CamelContext)
			service.camelContext = context
			def connected = new Fconnection(id:1)
			def notConnected = new Fconnection(id:2)
			def alsoConnected = new Fconnection(id:3)
			context.getRoute("1") >> Mock(org.apache.camel.Route)
			context.getRoute("3") >> Mock(org.apache.camel.Route)
		when:
			true
		then:
			service.getRouteStatus(connected) == RouteStatus.CONNECTED
			service.getRouteStatus(notConnected) == RouteStatus.NOT_CONNECTED
			service.getRouteStatus(alsoConnected) == RouteStatus.CONNECTED
	}
	
	def 'Created route has id equal to supplied Fconnection id'() {
		given:
			def service = new FconnectionService()
			def context = Mock(CamelContext)
			service.camelContext = context
			def connected = new Fconnection(id:1)
		when:
			service.createRoute(connected)
		then:
			1 * context.addRouteDefinitions({it.size() == 1 && it[0].id == '1'})
	}
}

