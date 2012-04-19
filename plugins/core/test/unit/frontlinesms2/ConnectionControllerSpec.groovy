package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(ConnectionController)
@Mock(Fconnection)
class ConnectionControllerSpec extends Specification {
	def "test that createRoute actually calls FconnectionService"() {
		setup:
			ConnectionController.metaClass.message { LinkedHashMap map-> map }
			def routesTriggered = []
			CreateRouteJob.metaClass.static.triggerNow = { LinkedHashMap map -> routesTriggered << map.connectionId }
			[new Fconnection(), new Fconnection()]*.save()
		when:
			params.id = 1 // mock the parameters for the request.  NB. mockParams cannot be overridden - only added and removed from
			controller.createRoute()
		then:
			routesTriggered == [1]
	}
}
