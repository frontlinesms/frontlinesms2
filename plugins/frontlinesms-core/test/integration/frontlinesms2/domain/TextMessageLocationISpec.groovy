package frontlinesms2.domain

import frontlinesms2.*

class TextMessageLocationISpec extends grails.plugin.spock.IntegrationSpec {
	private static final Date BASE_DATE = new Date(1322048115127L)
	
	def "getInboxMessages() returns the list of messages with inbound equal to true that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def inbox = TextMessage.inbox(false, false).list(sort:'src')
		then:
			inbox*.src == ["+254778899", "9544426444", "Alice", "Bob"]
			inbox.every { it.inbound }
			inbox.every { !it.archived }
	}

	def "should fetch starred messages from inbox"() {
		setup:
			createTestData()
		when:
			def inbox = TextMessage.inbox(['starred':true, 'archived': false])
		then:
			inbox.count() == 1
			inbox.list().every {it.starred}
			inbox.list().every { !it.archived }
	}

	def "check for offset and limit while fetching inbox messages"() {
		when:
			createTestData()
			def inboxMessages = TextMessage.inbox()
		then:
			inboxMessages.count() == 4
			inboxMessages.list(max:3, offset:0)*.src == ["+254778899", , "Bob", "9544426444"]
	}

	def "getSentMessages() returns the list of messages with inbound equal to false that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def sent = TextMessage.sent(false, false)
		then:
			sent.count() == 2
			sent.list().every { it.hasSent }
			sent.list().every { !it.archived }
	}

	def "should return starred sent messages"() {
		setup:
			createTestData()
		when:
			def sent = TextMessage.sent(true, false)
		then:
			assert sent.count() == 1
			sent.list()[0].hasSent
			sent.list()[0].starred
			sent.list().every { !it.archived }
	}

	def "check for offset and limit while fetching sent messages"() {
		setup:
			createTestData()
		when:
			assert 2 == TextMessage.sent(false, false).count()
			def firstSentMsg = TextMessage.sent(['archived': false, 'starred':false]).list(max: 1, offset: 0)
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
			results.list().every { !it.archived }
	}

	def "should fetch starred folder messages"() {
		setup:
			setUpFolderMessages()
		when:
			def results = Folder.findByName("home").getFolderMessages(true)
		then:
			results.list(sort:'src')*.src == ["Jack"]
			results.list().every { !it.archived }
	}
	
	def "check for offset and limit while fetching folder messages"() {
		setup:
			setUpFolderMessages()
		when:
			assert Folder.findByName("home").getFolderMessages(false).count() == 3
			def firstFolderMsg = Folder.findByName("home").getFolderMessages(false).list(max:1, offset: 0)
		then:
			firstFolderMsg.size() == 1
	}

	def "can fetch all pending messages"() {
		setup:
			new TextMessage(src:"src", text:"text", starred:true, date:new Date())
				.addToDispatches(dst:'1234567', status:DispatchStatus.PENDING)
				.save(failOnError:true)
			new TextMessage(src:"src", text:"text", date:new Date())
				.addToDispatches(dst:'1234567', status:DispatchStatus.FAILED)
				.save(failOnError:true)
		when:
			def results = TextMessage.listPending(false, [sort:"date", order:"desc"])
		then:
			results.size() == 2
			results.every { it.hasFailed || it.hasPending }
			results.every { !it.archived }
	}

	def "can fetch failed pending messages"() {
		setup:
			createTestData()
		when:
			def results = TextMessage.listPending(true, [sort:"date", order:"desc"])
		then:
			results.size() == 1
			results[0].hasFailed
			results.every { !it.archived }
	}

	def "can fetch starred deleted messages"() {
		setup:
			new TextMessage(src:'Bob', dst:'+254987654', inbound:true, text:'hi Bob', date:BASE_DATE - 4, isDeleted: true, starred: true).save(flush: true)
			new TextMessage(src:'Jim', dst:'+254987654', inbound:true, text:'hi Bob', date:BASE_DATE - 4, isDeleted: true).save(flush: true)
		when:
			def results = TextMessage.deleted(true)
		then:
			results.count() == 1
			results.list()[0].isDeleted
			results.list()[0].starred
			results.list().every { !it.archived }
	}

	def "can fetch all deleted messages"() {
		setup:
			TextMessage.build(isDeleted:true, date:BASE_DATE-4, starred:true)
			TextMessage.build(isDeleted:true, date:BASE_DATE-4)
		when:
			def results = TextMessage.deleted(false)
		then:
			results.count() == 2
			results.list()[0].isDeleted
			results.list().every { !it.archived }
	}

	def "check for offset and limit while fetching deleted messages"() {
		setup:
			TextMessage.build(date:new Date()-1, isDeleted:true)
			TextMessage.build(isDeleted:true)
		when:
			def firstDeletedMsg = TextMessage.deleted(false).list(max:1, offset: 0)
		then:
			firstDeletedMsg*.src == ['+254701234567']
	}
	
	def "can only archive ownerless messages, unless owner is archived"() {
		// FIXME: 
		// this actually tests "if message owner is archived, child message gets archived as well", which is related but different.
		when:
			createTestData()
			createPollTestData()
			def minime = TextMessage.findBySrc("Minime")
		then:
			!minime.archived
		when:
			minime.messageOwner.archive()
			minime.messageOwner.save(flush:true)
			minime.save(flush:true)
			minime.refresh()
		then:
			Poll.findByName("Miauow Mix").archived
			minime.archived
	}
	
	def "cannot un-archive a message if the owner is archived"() {
		when:
			createTestData()
			createPollTestData()
			def minime = TextMessage.findBySrc("Minime")
			minime.archived = false
			minime.messageOwner.archive()
			minime.messageOwner.save(flush:true)
			minime.archived = false
			minime.save(flush: true)
			minime.refresh()
		then:
			Poll.findByName("Miauow Mix").archived
			minime.archived
	}
	
	def createTestData() {
		// INCOMING MESSAGES
		TextMessage.build(src:'Bob', text:'hi Bob', date:BASE_DATE - 4000)
		TextMessage.build(src:'Alice', text:'hi Alice', date:BASE_DATE - 10000)
		TextMessage.build(src:'+254778899', text:'test', date:BASE_DATE - 3000)
		TextMessage.build(src:"9544426444", starred:true, date:BASE_DATE - 5000)
		
		// OUTGOING MESSAGES
		new TextMessage(src:'Mary', text:'hi Mary', date:BASE_DATE - 2)
				.addToDispatches(dst:'1234567', status:DispatchStatus.SENT, dateSent:new Date())
				.save(flush:true, failOnError:true)
		new TextMessage(src:'+254445566', text:'test', date:BASE_DATE - 1, starred:true)
				.addToDispatches(dst:'1234567', status:DispatchStatus.SENT, dateSent:new Date())
				.save(flush:true, failOnError:true)
		new TextMessage(src:"src", text:"text", date:new Date())
				.addToDispatches(dst:'1234567', status:DispatchStatus.FAILED)
				.save(flush:true, failOnError:true)
		new TextMessage(src:"src", text:"text", starred:true, date:new Date())
				.addToDispatches(dst:'1234567', status:DispatchStatus.PENDING)
				.save(flush:true, failOnError:true)
	}
	
	def createPollTestData() {
		TestData.createMiaowMixPoll()
	}

	private def setUpFolderMessages() {
		//FIXME: Need to remove.Test fails without this line.
		Folder.list()

		new Folder(name: 'home').save(flush: true)
		def folder = Folder.findByName('home')
		folder.addToMessages(TextMessage.build(src: "Bob", inbound:true, date:BASE_DATE - 14))
		folder.addToMessages(TextMessage.build(src: "Jim", inbound:true, date:BASE_DATE - 10))
		folder.addToMessages(TextMessage.build(src: "Jack", inbound:true, starred: true, date:BASE_DATE - 15))

		folder.save(flush: true)
	}
}
