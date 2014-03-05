package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*

class TextMessageISpec extends grails.plugin.spock.IntegrationSpec {
	final Date TEST_DATE = Date.parseToStringDate('Wed Sep 12 12:00:00 GMT 2012')

	def 'display name should be taken from Contact with matching mobile for incoming message'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			TextMessage m = TextMessage.build(src:'123')
		expect:
			m.refresh().displayName == 'bob'
	}

	def 'display name should be calculated for unsaved incoming messages'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			TextMessage m = TextMessage.buildWithoutSave(src:'123')
		expect:
			m.displayName == 'bob'
	}

	def 'display name should be calculated for unsaved outgoing message to single contact'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			TextMessage m = new TextMessage(src:'123', text:'')
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
		expect:
			m.displayName == 'bob'
	}

	def 'display name should be calculated for unsaved outgoing message to multiple contacts'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			TextMessage m = new TextMessage(src:'123', text:'')
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.addToDispatches(dst:'456', status:DispatchStatus.PENDING)
		expect:
			m.displayName == '2'
	}

	def 'display name should be src for incoming message if no matching contact'() {
		given:
			TextMessage m = TextMessage.build(src:'123')
		expect:
			m.refresh().displayName == '123'
	}

	def 'display for outgoing message should be taken from Contact with matching mobile if only one recipient'() {
		given:
			Contact.build(name:'bob', mobile:'123')
			TextMessage m = new TextMessage(text:'')
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.save(failOnError:true)
		expect:
			m.refresh().displayName == 'bob'
	}

	def 'outgoing display name should be dst if only one recipient but no matching contact'() {
		given:
			TextMessage m = new TextMessage(text:'')
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.save(failOnError:true)
		expect:
			m.refresh().displayName == '123'
	}

	def 'if multiple recipients display name should be the count of dispatches whether contacts exist or not'() {
		given:
			Contact.build(name:'adam', mobile:'123')
			Contact.build(name:'bob', mobile:'456')
			TextMessage m = new TextMessage(text:'')
					.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
					.addToDispatches(dst:'456', status:DispatchStatus.PENDING)
					.addToDispatches(dst:'789', status:DispatchStatus.PENDING)
					.save(failOnError:true)
		expect:
			m.refresh().displayName == '3'
	}

	def 'If any of a TextMessages dispatches has failed its status is HASFAILED'() {
		when:
			def message = buildWithDispatches(failedDispatch(), pendingDispatch(), sentDispatch())
		then:
			message.hasFailed
	}
	
	def 'If any of a TextMessages dispatches are pending, but none have failed its status is HASPENDING'() {
		when:
			def message = buildWithDispatches(pendingDispatch(), pendingDispatch(), sentDispatch())
		then:
			message.hasPending
	}
	
	def 'If all of a TextMessages dispatches have sent is status is HASSENT'() {
		when:
			def message = buildWithDispatches(sentDispatch(), sentDispatch(), sentDispatch())
		then:
			message.hasSent
	}
	
	def 'get deleted messages gets all messages with deleted flag'() {
		setup:
			3.times { TextMessage.build(isDeleted:true) }
			2.times { TextMessage.build() }
		when:
			def deletedMessages = TextMessage.deleted(false)
		then:
			deletedMessages.count() == 3
	}
	
	def "countAllMessages returns all message counts"() {
		setup:
			setUpMessages()
		when:
			def messageCounts = TextMessage.countAllMessages()
		then:
			messageCounts['inbox'] == 2
			messageCounts['sent'] == 4
			messageCounts['pending'] == 2
			messageCounts['deleted'] == 1
	}
	
	def "countUnreadMessages returns unread messages count"() {
		setup:
			TextMessage.build(read:true)
			TextMessage.build()
			TextMessage.build(archived:true)
		when:
			def unreadMessageCount = TextMessage.countUnreadMessages()
		then:
			unreadMessageCount == 1
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
				TextMessage.build(date:date, src:jessy.mobile)
				buildWithDispatches(
					// this dispatch should be counted because Jessy is in the target group
					new Dispatch(dst:jessy.mobile, status:DispatchStatus.SENT, dateSent:date))
			}
			3.times { TextMessage.build(date:TEST_DATE-1, src:jessy.mobile) }
			
			5.times {
				buildWithDispatches(
						// this dispatch should be counted because Jessy is in the target group
						new Dispatch(dst:jessy.mobile, status:DispatchStatus.SENT, dateSent:TEST_DATE),
						// this dispatch should be ignored because Lucy is not in the target group
						new Dispatch(dst:lucy.mobile, status:DispatchStatus.SENT, dateSent:TEST_DATE))
			}

		when:
			def asKey = { date -> date.format('dd/MM') }
			def stats = TextMessage.getMessageStats([groupInstance:fsharp, messageOwner:null,
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
			def messageFromAlice = TextMessage.build(src:'1234')
			def outBoundMessageToAlice = buildWithDispatches(
					new Dispatch(dst:'1234', status:DispatchStatus.SENT, dateSent:TEST_DATE))
		then:
			messageFromAlice.refresh().displayName == 'Alice'
			outBoundMessageToAlice.refresh().displayName == 'Alice'
	}
	
	def "cannot archive a message that has an owner without also archiving the owner" () {
		setup:
			def f = new Folder(name:'test').save(failOnError:true)
			def m = TextMessage.build()
			f.addToMessages(m)
			f.save()
		when:
			m.archived = true
		then:
			!m.validate()
	}
	
	def "when a new contact is created, all messages with that contacts mobile number should be updated"() {
		when:
			def message = TextMessage.build(src:'111')
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
			def message = TextMessage.build(src:'222')
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
			def m = TextMessage.build()
		when:
			m.archived = true
		then:
			m.validate()
	}
	
	def "TextMessage_owned should get archived vs non_archived"() {
		when:
			MessageOwner archivedOwner = getTestFolder(name:'test-archived', archived:true)
			TextMessage archived = createMessage(archived:true)
			addMessages archivedOwner, archived
			
			MessageOwner unarchivedOwner = getTestFolder(name:'test-unarchived')
			TextMessage notArchived = createMessage(archived:false)
			addMessages unarchivedOwner, notArchived
		then:
			TextMessage.owned(archivedOwner, false, true).list() == [archived]
			TextMessage.owned(unarchivedOwner, false, true).list() == [notArchived]
	}
	
	def "TextMessage_owned should get starred-and-non-starred vs only-starred"() {
		when:
			MessageOwner owner = testFolder
			TextMessage starred = createMessage(starred:true, dateDelta:1)
			TextMessage notStarred = createMessage(starred:false)
			addMessages owner, starred, notStarred
		then:
			TextMessage.owned(owner, true).list(sort:'date', order:'desc') == [starred]
			TextMessage.owned(owner, false).list(sort:'date', order:'desc') == [notStarred, starred]
	}
	
	def "TextMessage_owned should get sent-and-received vs sent-only"() {
		when:
			MessageOwner owner = testFolder
			TextMessage sent = createMessage(inbound:false, dateDelta:1)
			TextMessage received = createMessage(inbound:true)
			addMessages owner, sent, received
		then:
			TextMessage.owned(owner, false, null).list(sort:'date', order:'desc') == [received, sent]
			TextMessage.owned(owner, false, true).list(sort:'date', order:'desc') == [received]
	}

	def 'search page should display distinct messages'() {
		setup:
			createMessagesForSearch(60)
			def controller = new SearchController()
			controller.params.searchString = "sample"
			controller.params.max = 50
			controller.params.offset = 0
		when:
			def dataModel = controller.result()
		then:
			dataModel.interactionInstanceList.size() == dataModel.interactionInstanceList*.id.unique().size() &&
					dataModel.interactionInstanceList.size() == 50
	}

	@Unroll
	def 'pagination navigation should still work in search page'() {
		setup:
			createMessagesForSearch(80)
			def controller = new SearchController()
			controller.params.searchString = "sample"
			controller.params.max = max
			controller.params.offset = offset
		when:
			def dataModel = controller.result()
		then:
			dataModel.interactionInstanceList.size() == dataModel.interactionInstanceList*.id.unique().size()
			dataModel.interactionInstanceList*.id.containsAll(TextMessage.list(offset : offset,max : max,sort:"date", order:"desc")*.id)
		where:
			offset | max
			0      | 50
			50     | 50
	}

	def "if an FMessage's receivedOn connection is deleted, the field should update to null"() {
		when:
			def connection = SmslibFconnection.build(name:'MTN Dongle', port:'stormyPort')
			def message = TextMessage.build(src:'111', text:"bla")
			message.connectionId = connection.id
			message.save(failOnError:true)
		then:
			message.receivedOn.id == connection.id
		when:
			connection.delete()
		then:
			message.receivedOn == null
	}

	def "TextMessage.getOwnerDetail should return the value set by setMessageDetail in CustomActivity" (){
		when:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"1"))
			def replyStep = new ReplyActionStep().addToStepProperties(new StepProperty(key:"autoreplyText", value:"i will send this."))

			def customActivity = new CustomActivity(name:'Do it all')
				.addToSteps(joinStep)
				.addToSteps(replyStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)
 
			def incomingMessage = TextMessage.build(text:"incoming message", messageOwner: customActivity)
			def outgoingMessage = new TextMessage(text:'sending this message', inbound:false, date:TEST_DATE, messageOwner:customActivity)
							.addToDispatches(new Dispatch(dst:'234', status:DispatchStatus.PENDING)).save(failOnError:true)
			outgoingMessage.setMessageDetail(replyStep, incomingMessage.id)
		then:
			outgoingMessage.ownerDetail == incomingMessage.id.toString()
	}

	def "Setting the owner detail for webconnection" (){
		when:
			def uploadStep = new WebconnectionActionStep().addToStepProperties(new StepProperty(key:"url", value:"http://192.168.0.200"))
				.addToStepProperties(new StepProperty(key:"httpMethod", value:"GET"))
				.addToStepProperties(new StepProperty(key:"message", value:"I will upload you"))
				.addToStepProperties(new StepProperty(key:"source", value:"123123"))
			def customActivity = new CustomActivity(name:'Do it all')
				.addToSteps(uploadStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)
 
			def incomingMessage = TextMessage.build(text:"incoming message", messageOwner: customActivity)
			incomingMessage.setMessageDetail(uploadStep, 'success')
			incomingMessage.save(failOnError:true)
			incomingMessage.refresh()
		then:
			incomingMessage.getOwnerDetail() == "success"
	}

	def 'doing a TextMessage.findBySrc should give me youngest message'() {
		when:
			TextMessage.build(src:'111', text:'oldest')
			TextMessage.build(src:'111', text:'old')
			TextMessage.build(src:'111', text:'youngest')
		then:
			TextMessage.findBySrc('111', [sort: 'dateCreated', order:'desc']).text == 'youngest'
	}

	def 'calling the TextMessage.getOwnerType should return proper value'(){
		when:
			def customActivity = CustomActivity.build()
			def message = TextMessage.build()
		then:
			message.getOwnerType(customActivity) == MessageDetail.OwnerType.ACTIVITY
	}

	def "countUnreadMessages with an owner argument limits the count to only that owner's messages"() {
		setup:
			TextMessage.build(read:true)
			TextMessage.build()
			def autoReply = Autoreply.build()
			3.times { autoReply.addToMessages(TextMessage.build()) }
			TextMessage.build(archived:true)
		when:
			def inboxUnread = TextMessage.countUnreadMessages()
			def autoreplyUnread = TextMessage.countUnreadMessages(autoReply)
		then:
			inboxUnread == 1
			autoreplyUnread == 3
	}

	def "pendingAndNotFailed returns count of dispatches with PENDING status"() {
		setup:
			def m1 = TextMessage.buildWithoutSave(inbound:false, date: TEST_DATE - 10)
			4.times { m1.addToDispatches(Dispatch.build(status: DispatchStatus.PENDING)) }
			m1.addToDispatches(Dispatch.build(status: DispatchStatus.SENT, dateSent:TEST_DATE))
			m1.addToDispatches(Dispatch.build(status: DispatchStatus.FAILED))
		when:
			def pendingCount = TextMessage.pendingAndNotFailed.count()
		then:
			pendingCount == 4
	}
	
	private Folder getTestFolder(params=[]) {
		new Folder(name:params.name?:'test',
				archived:params.archived?:false).save(failOnError:true, flush:true)
	}
	
	private TextMessage createMessage(params) {
		def date = TEST_DATE - (params.dateDelta?:0)
		def m = new TextMessage(src:'1-POTATO',
				text:'',
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
	
	private void addMessages(MessageOwner o, TextMessage...messages) {
		messages.each { o.addToMessages(it).save(failOnError:true, flush:true) }
	}
	
	def createGroup(String n) {
		new Group(name:n).save(failOnError:true)
	}

	def createContact(String n, String a) {
		def c = new Contact(name: n, mobile: a)
		c.save(failOnError: true)
	}

	private TextMessage buildWithDispatches(Dispatch... dispatches) {
		def m = TextMessage.buildWithoutSave(inbound:false)
		dispatches.each { m.addToDispatches(it) }
		m.save(failOnError:true, flush:true)
	}

	private TextMessage buildOutgoing(params) {
		def m = buildWithDispatches(params.dispatches)
		if(params.containsKey('deleted')) m.isDeleted = params.deleted
		if(params.text) m.text = params.text
		return m
	}

	private def setUpMessages() {
		TextMessage.build(text:"An inbox message")
		TextMessage.build(text:"Another inbox message")
		buildWithDispatches(dispatch())
		buildWithDispatches(dispatch(), dispatch())
		buildOutgoing(deleted:true, dispatches:dispatch())
		buildOutgoing(text:'This msg will not show up in inbox view', dispatches:dispatch())
		buildWithDispatches(failedDispatch())
		buildWithDispatches(pendingDispatch())
	}

	private def createMessagesForSearch(int messageCount) {
		def message
		(1..messageCount).each {
			message = new TextMessage(src:'me', inbound:false, text:"sample message-${it}")
			(0..10).each { num ->
				message.addToDispatches(dst:"+25411663${num}", status:DispatchStatus.SENT, dateSent:TEST_DATE)
			}
			message.save(failOnError:true)
		}
	}

	private def dispatch() { sentDispatch() }
	private def sentDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.SENT, dateSent:TEST_DATE) }
	private def pendingDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.PENDING) }
	private def failedDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.FAILED) }
}

