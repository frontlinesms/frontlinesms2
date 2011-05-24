package frontlinesms2

class FmessageLocationSpec extends grails.plugin.spock.IntegrationSpec{
	def "getInboxMessages() returns a list of messages with inbound equal to true"() {
		setup:
			createTestMessages()
		when:
			def inbox = Fmessage.getInboxMessages()
		then:
			inbox.each {
				it.inbound == true
			}
			assert inbox.size() == 3

		cleanup:
			deleteTestMessages()
	}

	def "getSentMessages() returns a list of messages with inbound equal to false"() {
		setup:
			createTestMessages()
		when:
			def sent = Fmessage.getSentMessages()
		then:
			sent.each {
				it.inbound == false
			}
			assert sent.size() == 2
		cleanup:
			deleteTestMessages()
	}

	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test')].each() {
					it.inbound = true
					it.save(failOnError:true)
				}
		[new Fmessage(src:'Mary', dst:'+254112233', text:'hi Mary'),
				new Fmessage(src:'+254445566', dst:'+254112233', text:'test')].each() {
					it.inbound = false
					it.save(failOnError:true)
				}
	}
	static deleteTestMessages() {
		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}

