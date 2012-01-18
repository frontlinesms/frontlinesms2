package frontlinesms2.domain

import frontlinesms2.*

class FmessageLocationISpec extends grails.plugin.spock.IntegrationSpec {
	private static final Date BASE_DATE = new Date(1322048115127L)
	
	def "getInboxMessages() returns the list of messages with inbound equal to true that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.inbox(false, false).list(sort:'src')
		then:
			inbox*.src == ["+254778899", "9544426444", "Alice", "Bob"]
			inbox.every { it.inbound }
			inbox.every { !it.archived }
	}

	def "should fetch starred messages from inbox"() {
		setup:
			createTestData()
		when:
			def inbox = Fmessage.inbox(['starred':true, 'archived': false])
		then:
			inbox.count() == 1
			inbox.list().every {it.starred}
			inbox.list().every { !it.archived }
	}

	def "check for offset and limit while fetching inbox messages"() {
		when:
			createTestData()
			def inboxMessages = Fmessage.inbox()
		then:
			Fmessage.inbox().count() == 4
			Fmessage.inbox().list(max:3, offset:0)*.src == ["+254778899", , "Bob", "9544426444"]
	}

	def "getSentMessages() returns the list of messages with inbound equal to false that are not part of an activity"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.sent(false, false)
		then:
			sent.count() == 2
			sent.list().every { it.hasSent }
			sent.list().every { !it.archived }
	}

	def "should return starred sent messages"() {
		setup:
			createTestData()
		when:
			def sent = Fmessage.sent(true, false)
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
			createTestData()
		when:
			def results = Fmessage.pending(false)
		then:
			results.count() == 2
			results.list()*.every { it.hasFailed || it.hasPending }
			results.list().every { !it.archived }
	}

	def "can fetch failed pending messages"() {
		setup:
			createTestData()
		when:
			def results = Fmessage.pending(true)
		then:
		    results.count() == 1
			results.list()[0].hasFailed
			results.list().every { !it.archived }
	}

	def "can fetch starred deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', date:BASE_DATE - 4, deleted: true, starred: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', date:BASE_DATE - 4, deleted: true).save(flush: true)
		when:
			def results = Fmessage.deleted(true)
		then:
		    results.count() == 1
			results.list()[0].deleted
			results.list()[0].starred
			results.list().every { !it.archived }
	}

	def "can fetch all deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', date:BASE_DATE - 4, deleted: true, starred: true).save(flush: true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', date:BASE_DATE - 4, deleted: true).save(flush: true)
		when:
			def results = Fmessage.deleted(false)
		then:
		    results.count() == 2
			results.list()[0].deleted
			results.list().every { !it.archived }
	}

	def "check for offset and limit while fetching deleted messages"() {
		setup:
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', date:new Date() - 1, deleted:true).save(flush:true)
			new Fmessage(src:'Jim', dst:'+254987654', text:'hi Bob', date:new Date(), deleted:true).save(flush:true)
		when:
			def firstDeletedMsg = Fmessage.deleted(false).list(max:1, offset: 0)
		then:
			firstDeletedMsg*.src == ['Jim']
	}
	
	def "can only archive ownerless messages, unless owner is archived"() {
		when:
			createTestData()
			createPollTestData()
			def minime = Fmessage.findBySrc("Minime")
		then:
			!minime.archived
		when:
			minime.messageOwner.poll.archivePoll()
			minime.messageOwner.poll.save(flush: true)
			minime.save(flush: true)
		then:
			Poll.findByTitle("Miauow Mix").archived
			minime.archived
	}
	
	def "cannot un-archive a message if the owner is archived"() {
		when:
			createTestData()
			createPollTestData()
			def minime = Fmessage.findBySrc("Minime")
			minime.archived = false
			minime.messageOwner.poll.archivePoll()
			minime.messageOwner.poll.save(flush:true)
			minime.archived = false
			minime.save(flush: true)
			minime.refresh()
		then:
			Poll.findByTitle("Miauow Mix").archived
			minime.archived
	}
	
	def createTestData() {
		// INCOMING MESSAGES
		[new Fmessage(src:'Bob', text:'hi Bob', date:BASE_DATE - 4000),
				new Fmessage(src:'Alice', text:'hi Alice', date:BASE_DATE - 10000),
				new Fmessage(src:'+254778899', text:'test', date:BASE_DATE - 3000),
				new Fmessage(src: "9544426444", starred:true, date:BASE_DATE - 5000)].collect() {
			it.inbound = true
			it.save(failOnError:true)
		}
				
		def d1 = new Dispatch(dst:'1234567', status: DispatchStatus.SENT)
		def d2 = new Dispatch(dst:'1234567', status: DispatchStatus.SENT)
		def d3 = new Dispatch(dst:'1234567', status: DispatchStatus.FAILED)
		def d4 = new Dispatch(dst:'1234567', status: DispatchStatus.PENDING)
		
		// OUTGOING MESSAGES
		def m1 = new Fmessage(src:'Mary', text:'hi Mary', date:BASE_DATE - 2, hasSent:true)
		def m2 = new Fmessage(src:'+254445566', text:'test', date:BASE_DATE - 1, hasSent:true, starred: true)
		def m3 = new Fmessage(src:"src", text:"text",  hasFailed:true, date: new Date())
		def m4 = new Fmessage(src:"src", text:"text",  hasPending:true, starred: true, date: new Date())
		
		m1.addToDispatches(d1)
		m2.addToDispatches(d2)
		m3.addToDispatches(d3)
		m4.addToDispatches(d4)
		
		m1.save(flush:true)
		m2.save(flush:true)
		m3.save(flush:true)
		m4.save(flush:true)
	}
	
	def createPollTestData() {
		def chickenMessage = new Fmessage(src:'Barnabus', text:'i like chicken', inbound:true, date: new Date())
		def liverMessage = new Fmessage(src:'Minime', text:'i like liver', date: new Date(), inbound:true)
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
		folder.addToMessages(new Fmessage(src: "Bob", date:BASE_DATE - 14))
		folder.addToMessages(new Fmessage(src: "Jim", date:BASE_DATE - 10))
		folder.addToMessages(new Fmessage(src: "Jack", starred: true, date:BASE_DATE - 15))

		folder.save(flush: true)
	}
}
