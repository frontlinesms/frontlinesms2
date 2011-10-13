package frontlinesms2.domain

import frontlinesms2.*

class FmessageLocationISpec extends grails.plugin.spock.IntegrationSpec {
	def "getInboxMessages() returns the list of messages with inbound equal to true that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.inbox(false, false).list(sort:'src')
		then:
	        inbox*.src == ["+254778899", "9544426444", "Alice", "Bob"]
	        inbox.every {it.status == MessageStatus.INBOUND}
	        inbox.every {it.archived == false}
	}

	def "should fetch starred messages from inbox"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.inbox(['starred':true, 'archived': false])
		then:
			inbox.count() == 1
			inbox.list().every {it.starred}
			inbox.list().every {it.archived == false}
	}

	def "check for offset and limit while fetching inbox messages"() {
		setup:
			createTestData()
		when:
			assert 4 == Fmessage.inbox(false, false).count()
			def inboxMsgs = Fmessage.inbox(false, false)
			def firstThreeInboxMsgs = inboxMsgs.list(max: 3, offset: 0)
		then:
			firstThreeInboxMsgs.size() == 3
			firstThreeInboxMsgs*.src == ["9544426444", "+254778899", "Alice"]
	}

	def "getSentMessages() returns the list of messages with inbound equal to false that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.sent(false, false)
		then:
			sent.count() == 2
			sent.list().every { it.status == MessageStatus.SENT}
			sent.list().every { it.archived == false }
	}

	def "should return starred sent messages"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.sent(true, false)
		then:
			assert sent.count() == 1
			sent.list()[0].status == MessageStatus.SENT
			sent.list()[0].starred
			sent.list().every { it.archived == false }
	}

	def "check for offset and limit while fetching sent messages"() {
		setup:
			createTestData()
		when:
			assert 2 == Fmessage.sent(false, false).count()
			def firstSentMsg = Fmessage.sent(['archived': false, 'starred':false]).list(max: 1, offset: 0)
		then:
			firstSentMsg*.src == ['+254445566']
	}


	def "should return all folder messages ordered on date received"() {
		setup:
			setUpFolderMessages()
		when:
			def results = Folder.findByName("home").getFolderMessages(false)
		then:
			results.list(sort: 'src')*.src == ["Bob", "Jack", "Jim"]
			results.list().every { it.archived == false }
	}

	def "should fetch starred folder messages"() {
		setup:
			setUpFolderMessages()
		when:
			def results = Folder.findByName("home").getFolderMessages(true)
		then:
			results.list(sort:'src')*.src == ["Jack"]
			results.list().every {it.archived == false}
	}
	

	def "check for offset and limit while fetching folder messages"() {
		setup:
			setUpFolderMessages()
		when:
			assert Folder.findByName("home").getFolderMessages(false).count() == 3
			def firstFolderMsg = Folder.findByName("home").getFolderMessages(false).list(max:1, offset: 0)
		then:
			firstFolderMsg*.src == ['Jim']
	}

	def "can fetch all pending messages"() {
		setup:
			createTestData()
		when:
			def results = Fmessage.pending(false)
		then:
		    results.count() == 2
			results.list()*.status.containsAll([MessageStatus.SEND_FAILED, MessageStatus.SEND_PENDING])
			results.list().every {it.archived == false}
	}

	def "can fetch failed pending messages"() {
		setup:
			createTestData()
		when:
			def results = Fmessage.pending(true)
		then:
		    results.count() == 1
			results.list()[0].status == MessageStatus.SEND_FAILED
			results.list().every {it.archived == false}
	}

	def "check for offset and limit while fetching pending messages"() {
		setup:
			createTestData()
		when:
			def firstPendingMessage = Fmessage.pending(false).list(max: 1, offset: 0)
		then:
			firstPendingMessage*.dst == ['dst2']
	}

	def "can fetch starred deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true, starred: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
		when:
			def results = Fmessage.deleted(true)
		then:
		    results.count() == 1
			results.list()[0].deleted
			results.list()[0].starred
			results.list().every {it.archived == false}
	}

	def "can fetch all deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true, starred: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
		when:
			def results = Fmessage.deleted(false)
		then:
		    results.count() == 2
			results.list()[0].deleted
			results.list().every {it.archived == false}
	}

	def "check for offset and limit while fetching deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 4, deleted: true).save(flush: true)
		when:
			def firstDeletedMsg = Fmessage.deleted(false).list(max:1, offset: 0)
		then:
			firstDeletedMsg*.src == ['Jim']
	}
	
	def "can only archive ownerless messages, unless owner is archived"() {
		when:
			createTestData()
			def minime = Fmessage.findBySrc("Minime")
		then:
			minime.archived == false
		when:
			minime.messageOwner.poll.archivePoll()
			minime.messageOwner.poll.save(flush: true)
			minime.save(flush: true)
		then:
			Poll.findByTitle("Miauow Mix").archived == true
			minime.archived == true
	}
	
	def "cannot un-archive a message if the owner is archived"() {
		when:
			createTestData()
			def minime = Fmessage.findBySrc("Minime")
			minime.archived = false
			minime.messageOwner.poll.archivePoll()
			minime.messageOwner.poll.save(flush:true)
			minime.archived = false
			minime.save(flush: true)
			minime.refresh()
		then:
			Poll.findByTitle("Miauow Mix").archived == true
			minime.archived == true
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
		def poll = new Poll(title:'Miauow Mix').addToResponses(chickenResponse)
		poll.addToResponses(liverResponse).save(failOnError:true, flush:true)
		liverResponse.addToMessages(liverMessage).save(failOnError: true)
		chickenResponse.addToMessages(chickenMessage).save(failOnError: true)
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
