package frontlinesms2

class FmessageLocationSpec extends grails.plugin.spock.IntegrationSpec {
	def "getInboxMessages() returns the list of messages with inbound equal to true that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.getInboxMessages()
		then:
			inbox.each {
				it.inbound == true
			}
            inbox*.src == ["+254778899", "Bob", "Alice"]
		cleanup:
			deleteTestData()
	}

	def "getSentMessages() returns the list of messages with inbound equal to false that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.getSentMessages()
		then:
			sent.each {
				it.inbound == false
			}
			assert sent.size() == 2
		cleanup:
			deleteTestData()
	}

	def "should return all folder messages ordered on date received"() {
		setup:
			//FIXME: Need to remove.Test fails without this line.
			Folder.list()

			new Folder(name: 'home').save(flush: true)
			def folder = Folder.findByName('home')
			folder.addToMessages(new Fmessage(src: "Bob", dateReceived: new Date() - 14))
			folder.addToMessages(new Fmessage(src: "Jim", dateReceived: new Date() - 10))
			folder.save(flush: true)
		when:
			def results = Folder.findByName("home").folderMessages
		then:
			results*.src == ["Jim", "Bob"]
		cleanup:
			Folder.list()*.delete()
	}

	static createTestData() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice', dateReceived: new Date() - 5),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test', dateReceived: new Date() - 3)].each() {
					it.inbound = true
					it.save(failOnError:true)
				}
		[new Fmessage(src:'Mary', dst:'+254112233', text:'hi Mary', dateReceived: new Date() - 2),
				new Fmessage(src:'+254445566', dst:'+254112233', text:'test', dateReceived: new Date() - 1)].each() {
					it.inbound = false
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', inbound:true)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver', inbound:false)
        def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage).save(failOnError: true)
		chickenResponse.addToMessages(chickenMessage).save(failOnError: true)
        new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
	}

	static deleteTestData() {

		Poll.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}

		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}
