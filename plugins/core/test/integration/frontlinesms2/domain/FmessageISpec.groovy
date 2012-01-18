package frontlinesms2.domain

import frontlinesms2.*

class FmessageISpec extends grails.plugin.spock.IntegrationSpec {
	
	def 'If any of a Fmessages dispatches has failed its status is HASFAILED'() {
		when:
			def dispatch1 = new Dispatch(dst: '123456', status: DispatchStatus.FAILED)
			def dispatch2 = new Dispatch(dst: '123456', status: DispatchStatus.PENDING)
			def dispatch3 = new Dispatch(dst: '123456', status: DispatchStatus.SENT, dateSent: new Date())
			def message = new Fmessage(date:new Date())
			message.addToDispatches(dispatch1)
			message.addToDispatches(dispatch2)
			message.addToDispatches(dispatch3)
			message.save(failOnError: true)
			message.refresh()
		then:
			message.hasFailed == true
	}
	
	def 'If any of a Fmessages dispatches are pending, but none have failed its status is HASPENDING'() {
		when:
			def dispatch1 = new Dispatch(dst: '123456', status: DispatchStatus.PENDING)
			def dispatch2 = new Dispatch(dst: '123456', status: DispatchStatus.PENDING)
			def dispatch3 = new Dispatch(dst: '123456', status: DispatchStatus.SENT, dateSent: new Date())
			def message = new Fmessage(date:new Date())
			message.addToDispatches(dispatch1)
			message.addToDispatches(dispatch2)
			message.addToDispatches(dispatch3)
			message.save(failOnError: true)
			message.refresh()
		then:
			message.hasPending == true
	}
	
	def 'If all of a Fmessages dispatches have sent is status is HASSENT'() {
		when:
			def dispatch1 = new Dispatch(dst: '123456', status: DispatchStatus.SENT, dateSent: new Date())
			def dispatch2 = new Dispatch(dst: '123456', status: DispatchStatus.SENT, dateSent: new Date())
			def dispatch3 = new Dispatch(dst: '123456', status: DispatchStatus.SENT, dateSent: new Date())
			def message = new Fmessage(date:new Date())
			message.addToDispatches(dispatch1)
			message.addToDispatches(dispatch2)
			message.addToDispatches(dispatch3)
			message.save(failOnError: true)
			message.refresh()
		then:
			message.hasSent
	}
	
	def 'get deleted messages gets all messages with deleted flag'() {
		setup:
				(1..3).each {
				    new Fmessage(isDeleted:true,src:"+123456789", date: new Date()).save(flush:true)
				}
				(1..2).each {
					new Fmessage(isDeleted:false,src:"+987654321", date: new Date()).save(flush:true)
				}
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
			messageCounts['sent'] == 3
			messageCounts['pending'] == 2
			messageCounts['deleted'] == 1
	}
	
	def "countUnreadMessages returns unread messages count"() {
		setup:
			new Fmessage(src: '1234', inbound:true, isDeleted:false, text:'A read message', read:true, date: new Date()).save(flush:true)
			new Fmessage(src: '1234', inbound:true, isDeleted:false, text:'An unread message', read:false, date: new Date()).save(flush:true)
			new Fmessage(src: '1234', inbound:true, isDeleted:false, text:'An unread message', read:false, archived: true, date: new Date()).save(flush:true)
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
			def allMessages = Fmessage.search(search3).list()
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

	def "getMessageStats returns message traffic information for the filter criteria"() {
		setup:
			["Fsharp", 'Haskell'].each() { createGroup(it) }
			def fsharp = Group.findByName('Fsharp')
			def haskell = Group.findByName('Haskell')
			def jessy = createContact("Jessy", "+8139878055")
			def lucy = createContact("Lucy", "+8139878056")
			jessy.addToGroups(fsharp)
			lucy.addToGroups(haskell)
			
			int i =0
			(new Date()-6 ..new Date() + 5).each {
				new Fmessage(date: it, src:jessy.primaryMobile, inbound:true, text: "A message received on ${it}").save()
				new Fmessage(date: it, src:lucy.primaryMobile, hasSent:true, text: "A message sent on ${it}").save()
			}
			
			3.times {				
				new Fmessage(date: new Date()-1, src:jessy.primaryMobile, inbound:true, text: "Message {it}").save()
			}
			5.times {				
				new Fmessage(date: new Date()-1, src:jessy.primaryMobile, hasSent:true, text: "Message {it}").save()
			}

		when:
			def startDate = new Date()-2
			def endDate   = new Date()
			def messages = Fmessage.getMessageStats([groupInstance:fsharp, messageOwner:null, startDate:startDate, endDate:endDate])
		then:
			messages["${(startDate + 1).format('dd/MM')}"] == [sent:0, received:4]
			messages["${endDate.format('dd/MM')}"] == [sent:5, received:1]
	}

	def "new messages displayName are automatically given the matching contacts name"() {
		setup:
			new Contact(name: "Alice", primaryMobile:"1234", secondaryMobile: "4321").save(flush: true)
		when:
			def alice = Contact.findByName('Alice')
			def messageFromPrimaryNumber = new Fmessage(src:"1234", dst:"dst", inbound:true, date: new Date())
			def messageFromSecondaryNumber = new Fmessage(src:"4321", dst:"dst", inbound:true, date: new Date())
			def outBoundMessageToPrimaryNo = new Fmessage(src:"src", dst:"1234", hasSent:true, date: new Date())
			def outBoundMessageToSecondayNo = new Fmessage(src:"src", dst:"4321", hasSent:true, date: new Date())
			messageFromPrimaryNumber.save(flush: true)
			messageFromSecondaryNumber.save(flush: true)
			outBoundMessageToPrimaryNo.save(flush: true)
			outBoundMessageToSecondayNo.save(flush: true)
		then:
			messageFromPrimaryNumber.displayName == "Alice"
			messageFromSecondaryNumber.displayName == "Alice"
			outBoundMessageToPrimaryNo.displayName == "Alice"
			outBoundMessageToSecondayNo.displayName == "Alice"
	}
	
	def "cannot archive a message that has an owner" () {
		setup:
			def f = new Folder(name:'test').save(failOnError:true)
			def m = new Fmessage(src:'+123456789', text:'hello', date: new Date(), inbound: true).save(failOnError:true)
			f.addToMessages(m)
			f.save()
		when:
			m.archived = true
		then:
			!m.validate()
	}
	
	def "when a new contact is created, all messages with that contacts number should be updated"() {
		when:
			def message = new Fmessage(src: "number", inbound:true, date: new Date()).save(flush: true)
		then:
			message.displayName == "number";
			message.contactExists == false;
		when:
			new Contact(name: "Alice", primaryMobile:"number", secondaryMobile: "secondaryNo").save(flush: true)
		then:
			sleep 10000 // necessary since Fmessage is updated in a new thread
		when:
			message.refresh()
		then:
			message.displayName == "Alice"
			message.contactExists == true
	}
	
	def "can archive message when it has no message owner" () {
		setup:
			def m = new Fmessage(src:'+123456789', text:'hello', date: new Date(), inbound: true).save(failOnError:true)
		when:
			m.archived = true
		then:
			m.validate()
	}
	
	def createGroup(String n) {
		new Group(name: n).save(failOnError: true)
	}

	def createContact(String n, String a) {
		def c = new Contact(name: n, primaryMobile: a)
		c.save(failOnError: true)
	}

	private def setUpMessages() {
			new Fmessage(src: '1234', inbound:true, isDeleted:false, text:'An inbox message', date: new Date()).save(flush:true)
			new Fmessage(src: '1234', inbound:true, isDeleted:false, text:'Another inbox message', date: new Date()).save(flush:true)
			new Fmessage(src: '1234', hasSent:true, isDeleted:false, text:'A sent message', date: new Date()).save(flush:true)
			new Fmessage(hasSent: true, isDeleted:false, text:'Another sent message', date: new Date()).save(flush:true)
			new Fmessage(hasSent: true, isDeleted:true, text:'Deleted sent message', date: new Date()).save(flush:true)
			new Fmessage(hasSent: true, isDeleted:false, text:'This msg will not show up in inbox view', date: new Date()).save(flush:true)
			new Fmessage(hasFailed: true, isDeleted:false, text:'A sent failed message', date: new Date()).save(flush:true)
			new Fmessage(hasPending: true, isDeleted:false, text:'A pending message', date: new Date()).save(flush:true)
	}
}