package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*

class FmessageISpec extends grails.plugin.spock.IntegrationSpec {
	final Date TEST_DATE = new Date()

	def 'display name should be taken from Contact with matching mobile for incoming message'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			Fmessage m = Fmessage.build(src:'123')
		expect:
			m.refresh().displayName == 'bob'
	}

	def 'display name should be calculated for unsaved incoming messages'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			Fmessage m = Fmessage.buildWithoutSave(src:'123')
		expect:
			m.displayName == 'bob'
	}

	def 'display name should be calculated for unsaved outgoing message to single contact'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			Fmessage m = new Fmessage(src:'123', text:'')
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
		expect:
			m.displayName == 'bob'
	}

	def 'display name should be calculated for unsaved outgoing message to multiple contacts'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			Fmessage m = new Fmessage(src:'123', text:'')
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.addToDispatches(dst:'456', status:DispatchStatus.PENDING)
		expect:
			m.displayName == '2 recipients'
	}

	def 'display name should be src for incoming message if no matching contact'() {
		given:
			Fmessage m = Fmessage.build(src:'123')
		expect:
			m.refresh().displayName == '123'
	}

	def 'display for outgoing message should be taken from Contact with matching mobile if only one recipient'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			Fmessage m = new Fmessage()
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.save(failOnError:true)
		expect:
			m.refresh().displayName == 'To: bob'
	}

	def 'outgoing display name should be dst if only one recipient but no matching contact'() {
		given:
			Fmessage m = new Fmessage()
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.save(failOnError:true)
		expect:
			m.refresh().displayName == 'To: 123'
	}

	def 'if multiple recipients display name should be the count of dispatches whether contacts exist or not'() {
		given:
			Contact.build(name:'adam', mobile:'123')
			Contact.build(name:'bob', mobile:'456')
			Fmessage m = new Fmessage()
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.addToDispatches(dst:'456', status:DispatchStatus.PENDING)
					.addToDispatches(dst:'789', status:DispatchStatus.PENDING)
					.save(failOnError:true)
		expect:
			m.refresh().displayName == 'To 3 recipients'
	}

	def 'If any of a Fmessages dispatches has failed its status is HASFAILED'() {
		when:
			def message = buildWithDispatches(failedDispatch(), pendingDispatch(), sentDispatch())
		then:
			message.hasFailed
	}
	
	def 'If any of a Fmessages dispatches are pending, but none have failed its status is HASPENDING'() {
		when:
			def message = buildWithDispatches(pendingDispatch(), pendingDispatch(), sentDispatch())
		then:
			message.hasPending
	}
	
	def 'If all of a Fmessages dispatches have sent is status is HASSENT'() {
		when:
			def message = buildWithDispatches(sentDispatch(), sentDispatch(), sentDispatch())
		then:
			message.hasSent
	}
	
	def 'get deleted messages gets all messages with deleted flag'() {
		setup:
			3.times { Fmessage.build(isDeleted:true) }
			2.times { Fmessage.build() }
		when:
			def deletedMessages = Fmessage.deleted(false)
		then:
			deletedMessages.count() == 3
	}
	
	def "countAllMessages returns all message counts"() {
		setup:
			setUpMessages()
		when:
			def messageCounts = Fmessage.countAllMessages(['archived':false, 'starred': false])
		then:
			messageCounts['inbox'] == 2
			messageCounts['sent'] == 4
			messageCounts['pending'] == 2
			messageCounts['deleted'] == 1
	}
	
	def "countUnreadMessages returns unread messages count"() {
		setup:
			Fmessage.build(read:true)
			Fmessage.build()
			Fmessage.build(archived:true)
		when:
			def unreadMessageCount = Fmessage.countUnreadMessages()
		then:
			unreadMessageCount == 1
	}

	def "search returns results with a given max and offset"() {
		setup:
			setUpMessages()
			def search = new Search(searchString: 'inbox')
		when:
			def firstInboxMessage = Fmessage.search(search).list(max: 1,  offset:0)
			def firstTwoInboxMessages = Fmessage.search(search).list(max: 2, offset: 0)
			def allMsgsWithTheGivenSearchString = Fmessage.search(search).list()
		then:
			firstInboxMessage.size() == 1
			firstTwoInboxMessages.size() == 2
			allMsgsWithTheGivenSearchString.size() == 3
			Fmessage.search(search).count() == 3
	}

	def "messages are fetched based on message status"() {
		setup:
			setUpMessages()
			def search = new Search(status: ['INBOUND'])
			def search2 = new Search(status: ['SENT', 'PENDING', 'FAILED'])
			def search3 = new Search(searchString: "")
		when:
			def allInboundMessages = Fmessage.search(search).list()
			def allSentMessages = Fmessage.search(search2).list()
			def allMessages = Fmessage.search(search3).listDistinct()
		then:
			allInboundMessages*.every { it.inbound }
			allSentMessages*.every { !it.inbound }
			allMessages.size() == 7
	}
	
	def "searching for a group with no members does not throw an error"() {
		setup:
			def footballGroup = new Group(name: "football").save(flush: true)
			def search = new Search(group: footballGroup)
		when:
			def searchMessages = Fmessage.search(search).list()
			def searchMessagesCount = Fmessage.search(search).count()
		then:
			!searchMessages
			searchMessagesCount == 0
	}

	@Unroll
	def "searching for a partial name of a contact will match messages he has sent and received"() {
		given:
			def messages = [:]
			[robert:'123', bernie:'456', iane:'789'].each { contactName, mobile ->
				Contact.build(name:contactName, mobile:mobile)
				def sent = new Fmessage(text:'')
						.addToDispatches(dst:mobile, status:DispatchStatus.PENDING)
						.save(failOnError:true, flush:true)
				def received = Fmessage.build(src:mobile).save(failOnError:true, flush:true)
				messages[contactName] = [received, sent]
			}
		expect:
			Fmessage.search([contactString:contactString]).list(sort:'date', order:'desc') == contactNames.inject([]) { m, c -> m += messages[c] }
		where:
			contactString | contactNames
			'ROB'         | ['robert']
			'bER'         | ['bernie', 'robert']
			'i'           | ['iane', 'bernie']
			'e'           | ['iane', 'bernie', 'robert']
	}

	def "getMessageStats should return message traffic information for the filter criteria"() {
		setup:
			def fsharp = createGroup('Fsharp')
			def haskell = createGroup('Haskell')
			
			def jessy = createContact("Jessy", "+8139878055")
			jessy.addToGroups(fsharp)
			
			def lucy = createContact("Lucy", "+8139878056")
			lucy.addToGroups(haskell)
			
			(TEST_DATE-6..TEST_DATE+5).each { date ->
				Fmessage.build(date:date, src:jessy.mobile)
				buildWithDispatches(
					// this dispatch should be counted because Jessy is in the target group
					new Dispatch(dst:jessy.mobile, status:DispatchStatus.SENT, dateSent:date))
			}
			3.times { Fmessage.build(date:TEST_DATE-1, src:jessy.mobile) }
			
			5.times {
				buildWithDispatches(
						// this dispatch should be counted because Jessy is in the target group
						new Dispatch(dst:jessy.mobile, status:DispatchStatus.SENT, dateSent:TEST_DATE),
						// this dispatch should be ignored because Lucy is not in the target group
						new Dispatch(dst:lucy.mobile, status:DispatchStatus.SENT, dateSent:TEST_DATE))
			}

		when:
			def asKey = { date -> date.format('dd/MM') }
			def stats = Fmessage.getMessageStats([groupInstance:fsharp, messageOwner:null,
					startDate:TEST_DATE-2, endDate:TEST_DATE])
		then:
			stats.keySet().sort() == [TEST_DATE-2, TEST_DATE-1, TEST_DATE].collect { asKey it }
			stats["${asKey TEST_DATE-1}"] == [sent:1, received:4]
			stats["${asKey TEST_DATE}"] == [sent:6, received:1]
	}

	def "new messages displayName are automatically given the matching contacts name"() {
		setup:
			new Contact(name:"Alice", mobile:"1234")
					.save(failOnError:true, flush:true)
		when:
			def messageFromAlice = Fmessage.build(src:'1234')
			def outBoundMessageToAlice = buildWithDispatches(
					new Dispatch(dst:'1234', status:DispatchStatus.SENT, dateSent:TEST_DATE))
		then:
			messageFromAlice.refresh().displayName == 'Alice'
			outBoundMessageToAlice.refresh().displayName == 'To: Alice'
	}
	
	def "cannot archive a message that has an owner without also archiving the owner" () {
		setup:
			def f = new Folder(name:'test').save(failOnError:true)
			def m = Fmessage.build()
			f.addToMessages(m)
			f.save()
		when:
			m.archived = true
		then:
			!m.validate()
	}
	
	def "when a new contact is created, all messages with that contacts mobile number should be updated"() {
		when:
			def message = Fmessage.build(src:'111')
		then:
			message.displayName == '111'
		when:
			new Contact(name:"Alice", mobile:'111').save(failOnError:true, flush:true)
		then:
			message.refresh().displayName == "Alice"
	}

	def "when a contact is updated, all messages with that contacts primary number should be updated"() {
		when:
			def alice = new Contact(name:"Alice", mobile:'222').save(failOnError:true, flush:true)
			def message = Fmessage.build(src:'222')
		then:
			message.refresh().displayName == 'Alice'
		when:
			alice.mobile = '3'
			alice.save(failOnError:true, flush:true)
		then:
			message.refresh().displayName == '222'
	}
	
	def "can archive message when it has no message owner" () {
		setup:
			def m = Fmessage.build()
		when:
			m.archived = true
		then:
			m.validate()
	}
	
	def "Fmessage_owned should get archived vs non_archived"() {
		when:
			MessageOwner archivedOwner = getTestFolder(name:'test-archived', archived:true)
			Fmessage archived = createMessage(archived:true)
			addMessages archivedOwner, archived
			
			MessageOwner unarchivedOwner = getTestFolder(name:'test-unarchived')
			Fmessage notArchived = createMessage(archived:false)
			addMessages unarchivedOwner, notArchived
		then:
			Fmessage.owned(archivedOwner, false, true).list() == [archived]
			Fmessage.owned(unarchivedOwner, false, true).list() == [notArchived]
	}
	
	def "Fmessage_owned should get starred-and-non-starred vs only-starred"() {
		when:
			MessageOwner owner = testFolder
			Fmessage starred = createMessage(starred:true, dateDelta:1)
			Fmessage notStarred = createMessage(starred:false)
			addMessages owner, starred, notStarred
		then:
			Fmessage.owned(owner, true).list(sort:'date', order:'desc') == [starred]
			Fmessage.owned(owner, false).list(sort:'date', order:'desc') == [notStarred, starred]
	}
	
	def "Fmessage_owned should get sent-and-received vs sent-only"() {
		when:
			MessageOwner owner = testFolder
			Fmessage sent = createMessage(inbound:false, dateDelta:1)
			Fmessage received = createMessage(inbound:true)
			addMessages owner, sent, received
		then:
			Fmessage.owned(owner, false, true).list(sort:'date', order:'desc') == [received, sent]
			Fmessage.owned(owner, false, false).list(sort:'date', order:'desc') == [received]
	}
	
	private Folder getTestFolder(params=[]) {
		new Folder(name:params.name?:'test',
				archived:params.archived?:false).save(failOnError:true, flush:true)
	}
	
	private Fmessage createMessage(params) {
		def date = TEST_DATE - (params.dateDelta?:0)
		def m = new Fmessage(src:'1-POTATO',
				archived:params.archived?:false,
				starred:params.starred?:false,
				inbound:params.inbound!=null?params.inbound:true,
				date:date)
		if(params.inbound != null && !params.inbound) {
			m.addToDispatches(dst:'1234567890', status:DispatchStatus.SENT, dateSent:date)
		}
		println "Created with date $date"
		m.save(failOnError:true, flush:true)
	}
	
	private void addMessages(MessageOwner o, Fmessage...messages) {
		messages.each { o.addToMessages(it).save(failOnError:true, flush:true) }
	}
	
	def createGroup(String n) {
		new Group(name:n).save(failOnError:true)
	}

	def createContact(String n, String a) {
		def c = new Contact(name: n, mobile: a)
		c.save(failOnError: true)
	}

	private Fmessage buildWithDispatches(Dispatch... dispatches) {
		def m = Fmessage.buildWithoutSave(inbound:false)
		dispatches.each { m.addToDispatches(it) }
		m.save(failOnError:true, flush:true)
	}

	private Fmessage buildOutgoing(params) {
		def m = buildWithDispatches(params.dispatches)
		if(params.containsKey('deleted')) m.isDeleted = params.deleted
		if(params.text) m.text = params.text
		return m
	}

	private def setUpMessages() {
		Fmessage.build(text:"An inbox message")
		Fmessage.build(text:"Another inbox message")
		buildWithDispatches(dispatch())
		buildWithDispatches(dispatch(), dispatch())
		buildOutgoing(deleted:true, dispatches:dispatch())
		buildOutgoing(text:'This msg will not show up in inbox view', dispatches:dispatch())
		buildWithDispatches(failedDispatch())
		buildWithDispatches(pendingDispatch())
	}

	private def dispatch() { sentDispatch() }
	private def sentDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.SENT, dateSent:TEST_DATE) }
	private def pendingDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.PENDING) }
	private def failedDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.FAILED) }
}

