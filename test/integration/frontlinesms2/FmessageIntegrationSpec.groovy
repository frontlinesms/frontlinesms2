package frontlinesms2

import frontlinesms2.enums.MessageStatus

class FmessageIntegrationSpec extends grails.plugin.spock.IntegrationSpec {

	def "message doesn't have to have an activity"() {
		given:
		when:
			new Fmessage().save(failOnError:true, flush:true)
		then:
			Fmessage.count() == 1
		cleanup:
			Fmessage.findAll()*.delete()
	}

	def 'message can have an activity'() {
		given:
			new Poll(title:'Test poll').save()
			PollResponse response = new PollResponse(value:'yes').save()
		when:
			def m = new Fmessage(src:"+123456789", messageOwner:response).save()
		then:
			Fmessage.count() == 1
			Fmessage.get(m.id).messageOwner == response
		cleanup:
			Fmessage.findAll()*.delete()
			PollResponse.findAll()*.delete()
			Poll.findAll()*.delete()
	}
	
	def 'get deleted messages gets all messages with deleted flag'() {
		setup:
				(1..3).each {
				    new Fmessage(deleted:true,src:"+123456789").save(flush:true)
				}
				(1..2).each {
					new Fmessage(deleted:false,src:"+987654321").save(flush:true)
				}
		when:
			def deletedMessages = Fmessage.getDeletedMessages(['starred': false])
		then:
			deletedMessages.size() == 3
	}
	
	def "should return all message counts"() {
		setup:
			setUpMessages()
			
		when:
			def messageCounts = Fmessage.countAllMessages(['archived':false, 'starred': false])
		then:
			messageCounts['inbox'] == 2
			messageCounts['sent'] == 2
			messageCounts['pending'] == 2
			messageCounts['deleted'] == 1
	}
	
	def "should return unread messages count"() {
		setup:
			new Fmessage(status:MessageStatus.INBOUND, deleted:false, text:'A read message', read:true).save(flush:true)
			new Fmessage(status:MessageStatus.INBOUND,deleted:false, text:'An unread message', read:false).save(flush:true)
			new Fmessage(status:MessageStatus.INBOUND,deleted:false, text:'An unread message', read:false, archived: true).save(flush:true)
		when:
			def unreadMessageCount = Fmessage.countUnreadMessages()
		then:
			unreadMessageCount == 1
	}

	def "should return search results with a given max and offset"() {
		setup:
			setUpMessages()
		when:
			def firstInboxMessage = Fmessage.search("inbox", null, null, 1, 0)
			def firstTwoInboxMessages = Fmessage.search("inbox", null, null, 2, 0)
		then:
			firstInboxMessage.size() == 1
			firstTwoInboxMessages.size() == 2
			Fmessage.countAllSearchMessages("inbox", null, null) == 2
	}
	
	def "should get message traffic information for the filter criteria"() {
		setup:
			["Fsharp", 'Haskell'].each() { createGroup(it) }
			def fsharp = Group.findByName('Fsharp')
			def haskell = Group.findByName('Haskell')
			def jessy = createContact("Jessy", "+8139878055")
			def lucy = createContact("Lucy", "+8139878056")
			jessy.addToGroups(fsharp)
			lucy.addToGroups(haskell)
			
			(new Date("2011/10/10")..new Date("2011/10/20")).each {
				new Fmessage(dateReceived: it, dateCreated: it, src:jessy.primaryMobile, status: MessageStatus.INBOUND, text: "A message received on ${it}").save()
				new Fmessage(dateReceived: it, dateCreated: it, src:lucy.primaryMobile, status: MessageStatus.SENT, text: "A message sent on ${it}").save()
			}
			
			3.times {				
				new Fmessage(dateReceived: new Date("2011/10/12"), dateCreated: new Date("2011/10/12"), src:jessy.primaryMobile, status: MessageStatus.INBOUND, text: "Message {it}").save()
			}
			5.times {				
				new Fmessage(dateReceived: new Date("2011/10/12"), dateCreated: new Date("2011/10/12"), src:jessy.primaryMobile, status: MessageStatus.SENT, text: "Message {it}").save()
			}

		when:
			def startDate = new Date("2011/10/12")
			def endDate   = new Date("2011/10/13")
			def messages = Fmessage.getMessageStats(fsharp, null, startDate, endDate)
		then:
		messages == ["12/10":[Sent:5, Received:4], "13/10":[Sent:0, Received:1]]
	}

	def "should update message contact name"() {
		setup:
			new Contact(name: "Alice", primaryMobile:"primaryNo", secondaryMobile: "secondaryNo").save(flush: true)
		when:
			def messageFromPrimaryNumber = new Fmessage(src: "primaryNo", dst: "dst", status: MessageStatus.INBOUND)
			def messageFromSecondaryNumber = new Fmessage(src: "secondaryNo", dst: "dst", status: MessageStatus.INBOUND)
			def outBoundMessageToPrimaryNo = new Fmessage(src: "src", dst: "primaryNo", status: MessageStatus.SENT)
			def outBoundMessageToSecondayNo = new Fmessage(src: "src", dst: "secondaryNo", status: MessageStatus.SENT)
			messageFromPrimaryNumber.save(flush: true)
			messageFromSecondaryNumber.save(flush: true)
			outBoundMessageToPrimaryNo.save(flush: true)
			outBoundMessageToSecondayNo.save(flush: true)
		then:
			messageFromPrimaryNumber.contactName == "Alice"
			messageFromSecondaryNumber.contactName == "Alice"
			outBoundMessageToPrimaryNo.contactName == "Alice"
			outBoundMessageToSecondayNo.contactName == "Alice"
	}

	def createGroup(String n) {
		new Group(name: n).save(failOnError: true)
	}

	def createContact(String n, String a) {
		def c = new Contact(name: n, primaryMobile: a)
		c.save(failOnError: true)
	}

	private def setUpMessages() {
			new Fmessage(status:MessageStatus.INBOUND, deleted:false, text:'An inbox message').save(flush:true)
			new Fmessage(status:MessageStatus.INBOUND,deleted:false, text:'Another inbox message').save(flush:true)
			new Fmessage(status:MessageStatus.SENT, deleted:false, text:'A sent message').save(flush:true)
			new Fmessage(status:MessageStatus.SENT,deleted:false, text:'Another sent message').save(flush:true)
			new Fmessage(status:MessageStatus.SENT,deleted:true, text:'Deleted sent message').save(flush:true)
			new Fmessage(status:MessageStatus.SEND_FAILED, deleted:false, text:'A sent failed message').save(flush:true)
			new Fmessage(status:MessageStatus.SEND_PENDING,deleted:false, text:'A pending message').save(flush:true)
	}
}
