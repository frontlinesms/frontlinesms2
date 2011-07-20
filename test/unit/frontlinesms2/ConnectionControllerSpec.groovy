package frontlinesms2

import grails.plugin.spock.*

class ConnectionControllerSpec extends ControllerSpec {
	def "default action is 'list'"() {
		when:
			controller.index()
		then:
			controller.redirectArgs.controller == 'connection' || !controller.redirectArgs.controller
			controller.redirectArgs.action == 'list'
	}

	def "test that createRoute actually calls FconnectionService"() {
		setup:
//			controller.metaClass.message = { LinkedHashMap m -> "unresolved i18n message '${m}'"}
			def fconnection1 = new Fconnection() // we need a ref to this to make sure it is passed to our mock service
			def fconnection2 = new Fconnection()
			mockDomain(Fconnection, [fconnection1, fconnection2]) // mock the Fconnection domain, as it will be needed to retrieve the Fconnection instance to pass to FconnectionService instance
			def service = Mock(FconnectionService)
			controller.fconnectionService = service // inject mock service into the instance of ConnectionController under test so we can verify the createRoute() method is called
		when:
			mockParams.id = 1 // mock the parameters for the request.  NB. mockParams cannot be overridden - only added and removed from
			controller.createRoute()
		then:
			1 * service.createRoute(fconnection1) // Verify that createRoute was called on the correct Fconnection instance
	}
}
