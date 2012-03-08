package frontlinesms2

import grails.plugin.spock.*

class ConnectionControllerSpec extends ControllerSpec {
	def "test that createRoute actually calls FconnectionService"() {
		setup:
			registerMetaClass(CreateRouteJob)
			ConnectionController.metaClass.message {LinkedHashMap map-> map}
			def routesTriggered = []
			CreateRouteJob.metaClass.static.triggerNow = { LinkedHashMap map -> routesTriggered << map.connectionId }
			mockDomain(Fconnection, [new Fconnection(), new Fconnection()])
		when:
			mockParams.id = 1 // mock the parameters for the request.  NB. mockParams cannot be overridden - only added and removed from
			controller.createRoute()
		then:
			routesTriggered == [1]
	}
}
