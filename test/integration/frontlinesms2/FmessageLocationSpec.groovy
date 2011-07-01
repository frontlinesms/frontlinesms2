package frontlinesms2

import frontlinesms2.enums.MessageStatus

class FmessageLocationSpec extends grails.plugin.spock.IntegrationSpec {
	def "getInboxMessages() returns the list of messages with inbound equal to true that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.getInboxMessages(false)
		then:
	        inbox*.src == ["+254778899", "Bob", "Alice", "9544426444"]
	        inbox.every {it.status == MessageStatus.INBOUND}
		cleanup:
			deleteTestData()
	}

	def "should fetch starred messages from inbox"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.getInboxMessages(true)
		then:
			inbox.size() == 1
			inbox.every {it.starred}
		cleanup:
			deleteTestData()
	}

	def "getSentMessages() returns the list of messages with inbound equal to false that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.getSentMessages(false)
		then:
			assert sent.size() == 2
			sent.every { it.status == MessageStatus.SENT}
		cleanup:
			deleteTestData()
	}

	def "should return starred sent messages"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.getSentMessages(true)
		then:
			assert sent.size() == 1
			sent[0].status == MessageStatus.SENT
			sent[0].starred
		cleanup:
			deleteTestData()
	}

	def "should return all folder messages ordered on date received"() {
		setup:
			setUpFolderMessages()
		when:
			def results = Folder.findByName("home").getFolderMessages(false)
		then:
			results*.src == ["Jim", "Bob", "Jack"]
		cleanup:
			Folder.list()*.delete()
	}

	def "should fetch starred folder messages"() {
		setup:
			setUpFolderMessages()
		when:
			def results = Folder.findByName("home").getFolderMessages(true)
		then:
			results*.src == ["Jack"]
		cleanup:
			Folder.list()*.delete()
	}

	def "should fetch all pending messages"() {
		setup:
			createTestData()
		when:
			def results = Fmessage.getPendingMessages(false)
		then:
		    results.size() == 2
			results*.status.containsAll([MessageStatus.SEND_FAILED, MessageStatus.SEND_PENDING])
		cleanup:
			deleteTestData()
	}

	def "should fetch starred pending messages"() {
		setup:
			createTestData()
		when:
			def results = Fmessage.getPendingMessages(true)
		then:
		    results.size() == 1
			results[0].status == MessageStatus.SEND_PENDING
			results[0].starred
		cleanup:
			deleteTestData()
	}

	def "should fetch starred deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true, starred: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
		when:
			def results = Fmessage.getDeletedMessages(true)
		then:
		    results.size() == 1
			results[0].deleted
			results[0].starred
		cleanup:
			deleteTestData()
	}

	def "should fetch deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true, starred: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
		when:
			def results = Fmessage.getDeletedMessages(false)
		then:
		    results.size() == 2
			results[0].deleted
		cleanup:
			deleteTestData()
	}

	static createTestData() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice', dateReceived: new Date() - 5),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test', dateReceived: new Date() - 3),
				new Fmessage(src: "9544426444", dst: "34562265", starred: true, dateReceived: new Date() - 10)].each() {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true)
				}
		[new Fmessage(src:'Mary', dst:'+254112233', text:'hi Mary', dateReceived: new Date() - 2, status:MessageStatus.SENT),
				new Fmessage(src:'+254445566', dst:'+254112233', text:'test', dateReceived: new Date() - 1, status:MessageStatus.SENT, starred: true)].each() {
					it.save(failOnError:true)
				}
		[new Fmessage(src:"src", dst:"dst1", text:"text",  status: MessageStatus.SEND_FAILED),
			new Fmessage(src:"src", dst:"dst2", text:"text",  status: MessageStatus.SEND_PENDING, starred: true)].each {
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

	private def setUpFolderMessages() {
		//FIXME: Need to remove.Test fails without this line.
		Folder.list()

		new Folder(name: 'home').save(flush: true)
		def folder = Folder.findByName('home')
		folder.addToMessages(new Fmessage(src: "Bob", dateReceived: new Date() - 14))
		folder.addToMessages(new Fmessage(src: "Jim", dateReceived: new Date() - 10))
		folder.addToMessages(new Fmessage(src: "Jack", starred: true, dateReceived: new Date() - 15))

		folder.save(flush: true)
	}
}
