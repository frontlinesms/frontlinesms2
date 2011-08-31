package frontlinesms2

import grails.plugin.spock.ControllerSpec


class StatusControllerSpec extends ControllerSpec  {

	def "should display the over all connection status when one of the device is not working"() {
		setup:
			controller.metaClass.fetchAllStatus = { -> ['device1' : ConnectionStatus.NOT_CONNECTED,
														'device2' : ConnectionStatus.CONNECTED,
														'device3' : ConnectionStatus.ERROR]}
		when:
			controller.trafficLightIndicator()
		then:
			renderArgs.text == ConnectionStatus.NOT_CONNECTED.getIndicator()
	}
	
	def "should display the over all connection status when one of the device has a problem"() {
		setup:
			controller.metaClass.fetchAllStatus = { -> ['device1' : ConnectionStatus.ERROR,
														'device2' : ConnectionStatus.CONNECTED,
														'device3' : ConnectionStatus.ERROR]}
		when:
			controller.trafficLightIndicator()
		then:
			renderArgs.text== ConnectionStatus.ERROR.getIndicator()
	}
	
	def "should display the over all connection status all the devices are working properly"() {
		setup:
			controller.metaClass.fetchAllStatus = { -> ['device1' : ConnectionStatus.CONNECTED,
														'device2' : ConnectionStatus.CONNECTED,
														'device3' : ConnectionStatus.CONNECTED]}
		when:
			controller.trafficLightIndicator()
		then:
			renderArgs.text == ConnectionStatus.CONNECTED.getIndicator()
	}
}
