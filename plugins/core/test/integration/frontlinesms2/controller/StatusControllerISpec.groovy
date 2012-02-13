package frontlinesms2.controller

import frontlinesms2.*

class StatusControllerISpec extends grails.plugin.spock.IntegrationSpec {
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
			model.connectionInstanceTotal == 2
			model.connectionInstanceList == [SmslibFconnection.findByName('MTN Dongle'), EmailFconnection.findByUsername('mr.testy@zoho.com')]
		when:
			SmslibFconnection.findByName('MTN Dongle').delete(flush:true)
			model = controller.show()
		then:
			model.connectionInstanceTotal == 1
			model.connectionInstanceList == [EmailFconnection.findByUsername('mr.testy@zoho.com')]
	}
	
	def "message start dates should be inclusive"() {
		given:
			def sentDate = createDate(2011, 10, 18, 23, 58, 59)
			def m1 = new Fmessage(src:"src1", date:createDate(2011, 10, 18, 0, 0, 1), inbound:true).save(flush:true, failOnError:true)
			def m2 = new Fmessage(src:"src2", date:sentDate, hasSent:true, inbound: false)
			def d = new Dispatch(dst:'123', status:DispatchStatus.SENT, dateSent:sentDate)
			m2.addToDispatches(d)
			m2.save(flush:true, failOnError:true)
		when:
			// TODO set start and end dates to the same day as messages were sent
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 0, 0, 0)
			controller.params.endDate = createDate(2011, 10, 18, 23, 59, 59)
			def model = controller.show()
		then:
			model.messageStats.sent == [1]
			model.messageStats.received == [1]
			
		when:
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 23, 58, 59)
			controller.params.endDate = createDate(2011, 10, 19, 0, 0, 0)
			model = controller.show()
		then:
			model.messageStats.sent == [1]
			model.messageStats.received == [1]
		when:
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 0, 0, 0)
			controller.params.endDate = createDate(2011, 10, 19, 23, 57, 59)
			model = controller.show()
		then:
			model.messageStats.sent == [1, 0]
			model.messageStats.received == [1, 0]
	}
	
	def createDate(int year, int month, int date, int hour, int minute, int second) {
		def calc = Calendar.getInstance()
		calc.set(year, month, date, hour, minute, second)
		calc.getTime()
	}
}
