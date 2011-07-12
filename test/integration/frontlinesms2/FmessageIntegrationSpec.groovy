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
			deletedMessages.size == 3
	}
	
	def "should return all message counts"() {
		setup:
			Fmessage inboxMessage = new Fmessage(status:MessageStatus.INBOUND, deleted:false, text:'An inbox message').save(flush:true)
			Fmessage anotherInboxMessage = new Fmessage(status:MessageStatus.INBOUND,deleted:false, text:'Another inbox message').save(flush:true)
			
			Fmessage sentMessage = new Fmessage(status:MessageStatus.SENT, deleted:false, text:'A sent message').save(flush:true)
			Fmessage anotherSentMessage = new Fmessage(status:MessageStatus.SENT,deleted:false, text:'Another sent message').save(flush:true)
			Fmessage deletedSentMessage = new Fmessage(status:MessageStatus.SENT,deleted:true, text:'Deleted sent message').save(flush:true)
			
			Fmessage sentFailedMessage = new Fmessage(status:MessageStatus.SEND_FAILED, deleted:false, text:'A sent failed message').save(flush:true)
			Fmessage sentPendingMessage = new Fmessage(status:MessageStatus.SEND_PENDING,deleted:false, text:'A pending message').save(flush:true)
			
		when:
			def messageCounts = Fmessage.countAllMessages(false)
		then:
			messageCounts['inbox'] == 2
			messageCounts['sent'] == 2
			messageCounts['pending'] == 2
			messageCounts['deleted'] == 1
	}
}
