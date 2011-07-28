package frontlinesms2

import frontlinesms2.enums.MessageStatus

class FmessageIntegrationSpec extends grails.plugin.spock.IntegrationSpec {

	def 'get deleted messages gets all messages with deleted flag'() {
		setup:
				(1..3).each {
				    new Fmessage(deleted:true).save(flush:true)
				}
				(1..2).each {
					new Fmessage(deleted:false).save(flush:true)
				}
		when:
			def deletedMessages = Fmessage.getDeletedMessages(false)
		then:
			deletedMessages.size() == 3
	}
	
	def "should return all message counts"() {
		setup:
			setUpMessages()
			
		when:
			def messageCounts = Fmessage.countAllMessages(false)
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
