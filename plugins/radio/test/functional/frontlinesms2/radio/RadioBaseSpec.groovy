package frontlinesms2.radio

import frontlinesms2.*

class RadioBaseSpec extends geb.spock.GebReportingSpec {
	
	static createRadioShows() {
		[new RadioShow(name:"Morning Show"),
			new RadioShow(name: "Test")].each {it.save(failOnError:true, flush:true)}
		
	}
	
	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test')].each() {
					it.inbound = true
					it.messageOwner = RadioShow.findByName("Morning Show")
					it.save(flush:true, failOnError:true)
				}
	}
}