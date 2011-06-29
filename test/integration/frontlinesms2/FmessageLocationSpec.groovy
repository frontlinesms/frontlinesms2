package frontlinesms2

import frontlinesms2.enums.MessageStatus

class FmessageLocationSpec extends grails.plugin.spock.IntegrationSpec {
	def "getInboxMessages() returns the list of messages with inbound equal to true that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.getInboxMessages()
		then:
	        inbox*.src == ["+254778899", "Bob", "Alice"]
	        inbox*.status == [MessageStatus.INBOUND, MessageStatus.INBOUND, MessageStatus.INBOUND]
		cleanup:
			deleteTestData()
	}

	def "getSentMessages() returns the list of messages with inbound equal to false that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.getSentMessages()
		then:
			assert sent.size() == 2
			sent*.status == [MessageStatus.SENT, MessageStatus.SENT]
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

	def "should fetch all pending messages"() {
		setup:
			new Fmessage(src:"src", dst:"dst1", text:"text",  status: MessageStatus.SEND_FAILED).save(flush: true)
			new Fmessage(src:"src", dst:"dst2", text:"text",  status: MessageStatus.SEND_PENDING).save(flush: true)
			new Fmessage(src:"src", dst:"dst3", text:"text",  status: MessageStatus.SENT).save(flush: true)
			new Fmessage(src:"src", dst:"dst4", text:"text").save(flush: true)
		when:
		    assert Fmessage.count() == 4
			def results = Fmessage.getPendingMessages()
		then:
		    results.size() == 2
			results*.status.containsAll([MessageStatus.SEND_FAILED, MessageStatus.SEND_PENDING])
	}

	static createTestData() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice', dateReceived: new Date() - 5),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test', dateReceived: new Date() - 3)].each() {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true)
				}
		[new Fmessage(src:'Mary', dst:'+254112233', text:'hi Mary', dateReceived: new Date() - 2, status:MessageStatus.SENT),
				new Fmessage(src:'+254445566', dst:'+254112233', text:'test', dateReceived: new Date() - 1, status:MessageStatus.SENT)].each() {
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', status:MessageStatus.INBOUND)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver')
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
