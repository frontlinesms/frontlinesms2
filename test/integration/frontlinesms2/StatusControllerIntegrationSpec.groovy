package frontlinesms2


class StatusControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new StatusController()
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
			}
	}
	
	def "should return a list of all available connections"() {
		when:
			def model = controller.show()
		then:
			model.fconnectionInstanceTotal == 2
			model.connectionInstanceList == [SmslibFconnection.findByName('MTN Dongle'), EmailFconnection.findByUsername('mr.testy@zoho.com')]
		when:
			SmslibFconnection.findByName('MTN Dongle').delete(flush:true)
			model = controller.show()
		then:
			model.fconnectionInstanceTotal == 1
			model.connectionInstanceList == [EmailFconnection.findByUsername('mr.testy@zoho.com')]
	}
}
