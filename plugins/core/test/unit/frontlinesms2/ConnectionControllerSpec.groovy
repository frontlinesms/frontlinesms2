package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(ConnectionController)
@Mock([Fconnection, FconnectionService])
class ConnectionControllerSpec extends Specification {
	def "test that createRoute actually calls FconnectionService"() {
		setup:
			def routesTriggered = []
			CreateRouteJob.metaClass.static.triggerNow = { LinkedHashMap map -> routesTriggered << map.connectionId }
			[new Fconnection(), new Fconnection()]*.save()
		when:
			params.id = 1 // mock the parameters for the request.  NB. mockParams cannot be overridden - only added and removed from
			controller.createRoute()
		then:
			routesTriggered == [1]
	}

	def "delete should delete an inactive Fconnection"() {
		given:
			def c = buildTestConnection(RouteStatus.NOT_CONNECTED)
			params.id = c.id
		when:
			controller.delete()
		then:
			notThrown(RuntimeException)
			SmslibFconnection.findAll() == []
	}

	def "delete should throw an exception for an active fconnection"() {
		given:
			def c = buildTestConnection(RouteStatus.CONNECTED)
			params.id = c.id
		when:
			controller.delete()
		then:
			thrown(RuntimeException)
			SmslibFconnection.findAll() == [c]
	}

	private def buildTestConnection(status) {
		def c = new Fconnection(name:'test').save(failOnError:true)
		def fconnectionService = Mock(FconnectionService)
		fconnectionService.getRouteStatus(_) >> { connection -> status }
		c.fconnectionService = fconnectionService
		assert c.fconnectionService != null
		return c
	}
}

