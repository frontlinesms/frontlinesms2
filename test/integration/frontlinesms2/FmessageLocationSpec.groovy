package frontlinesms2


class FmessageLocationSpec extends grails.plugin.spock.IntegrationSpec {
	def "getInboxMessages() returns the list of messages with inbound equal to true that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.getInboxMessages(['starred':false, 'archived': false])
		then:
	        inbox*.src == ["+254778899", "Bob", "Alice", "9544426444"]
	        inbox.every {it.status == MessageStatus.INBOUND}
	        inbox.every {it.archived == false}
	}

	def "should fetch starred messages from inbox"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.getInboxMessages(['starred':true, 'archived': false])
		then:
			inbox.size() == 1
			inbox.every {it.starred}
			inbox.every {it.archived == false}
	}

	def "check for offset and limit while fetching inbox messages"() {
		setup:
			createTestData()
		when:
			assert 4 == Fmessage.countInboxMessages(['archived': false, 'starred': false])
			def firstThreeInboxMsgs = Fmessage.getInboxMessages(['archived': false,'starred':false, 'max': 3, 'offset': 0])
		then:
			firstThreeInboxMsgs.size() == 3
			firstThreeInboxMsgs*.src == ["+254778899", "Bob", "Alice"]
	}

	def "getSentMessages() returns the list of messages with inbound equal to false that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.getSentMessages(['archived': false, 'starred':false])
		then:
			assert sent.size() == 2
			sent.every { it.status == MessageStatus.SENT}
			sent.every {it.archived == false}
	}

	def "should return starred sent messages"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.getSentMessages(['archived': false, 'starred':true])
		then:
			assert sent.size() == 1
			sent[0].status == MessageStatus.SENT
			sent[0].starred
			sent.every {it.archived == false}
	}

	def "check for offset and limit while fetching sent messages"() {
		setup:
			createTestData()
		when:
			assert 2 == Fmessage.countSentMessages(['archived': false, 'starred': false])
			def firstSentMsg = Fmessage.getSentMessages(['archived': false, 'starred':false, 'max': 1, 'offset': 0])
		then:
			firstSentMsg*.src == ['+254445566']
	}


	def "should return all folder messages ordered on date received"() {
		setup:
			setUpFolderMessages()
		when:
			def results = Folder.findByName("home").getFolderMessages(['starred':false])
		then:
			results*.src == ["Jim", "Bob", "Jack"]
			results.every {it.archived == false}
	}

	def "should fetch starred folder messages"() {
		setup:
			setUpFolderMessages()
		when:
			def results = Folder.findByName("home").getFolderMessages(['starred':true])
		then:
			results*.src == ["Jack"]
			results.every {it.archived == false}
	}
	

	def "check for offset and limit while fetching folder messages"() {
		setup:
			setUpFolderMessages()
		when:
			assert 3 == Folder.findByName("home").countMessages(false)
			def firstFolderMsg = Folder.findByName("home").getFolderMessages(['starred':false, 'max':1, 'offset': 0])
		then:
			firstFolderMsg*.src == ['Jim']
	}

	def "should fetch all pending messages"() {
		setup:
			createTestData()
		when:
			def results = Fmessage.getPendingMessages(['starred':false])
		then:
		    results.size() == 2
			results*.status.containsAll([MessageStatus.SEND_FAILED, MessageStatus.SEND_PENDING])
			results.every {it.archived == false}
	}

	def "should fetch starred pending messages"() {
		setup:
			createTestData()
		when:
			def results = Fmessage.getPendingMessages(['starred':true])
		then:
		    results.size() == 1
			results[0].status == MessageStatus.SEND_PENDING
			results[0].starred
			results.every {it.archived == false}
	}

	def "check for offset and limit while fetching pending messages"() {
		setup:
			createTestData()
		when:
			def firstPendingMessage = Fmessage.getPendingMessages(['starred':false, 'max':1, 'offset': 0])
		then:
			firstPendingMessage*.dst == ['dst1']
	}

	def "should fetch starred deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true, starred: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
		when:
			def results = Fmessage.getDeletedMessages(['starred':true])
		then:
		    results.size() == 1
			results[0].deleted
			results[0].starred
			results.every {it.archived == false}
	}

	def "should fetch deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true, starred: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
		when:
			def results = Fmessage.getDeletedMessages(['starred':false])
		then:
		    results.size() == 2
			results[0].deleted
			results.every {it.archived == false}
	}

	def "check for offset and limit while fetching deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
		when:
			def firstDeletedMsg = Fmessage.getDeletedMessages(['starred':false, 'max':1, 'offset': 0])
		then:
			firstDeletedMsg*.src == ['Jim']
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
