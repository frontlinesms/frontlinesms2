package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.utils.*

@Mixin(frontlinesms2.utils.GebUtil)
class MessageListSpec extends grails.plugin.geb.GebSpec {

    def 'button to view inbox messages exists and goes to INBOX page'() {
        when:
          to PageMessageInbox
        then:
			bodyMenu.messageSection("Inbox").text() == 'Inbox'
		when:
			bodyMenu.messageSection("Inbox").click()
		then:
			waitFor { title == "Inbox" }
    }

    def 'button to view sent messages exists and goes to SENT page'() {
        when:
          to PageMessageInbox
        then:
			bodyMenu.messageSection("Sent").text() == 'Sent'
		when:
			bodyMenu.messageSection("Sent").click()
		then:
			waitFor { title == "Sent" }
    }
    
    def 'when in inbox, Inbox menu item is highlighted'() {
        when:
			to PageMessageInbox
        then:
			bodyMenu.selected == "inbox"
	}

	def 'when viewing Sent Items, Sent Items menu item is hilighted'() {
        when:
			to PageMessageSent
        then:
			bodyMenu.selected == "sent"
    }

	def 'Messages tab should have unread messages count next to it'() {
		given:
			createReadUnreadMessages()
		when:
			to PageMessageInbox
		then:
			tabs.unreadcount == 2
	}
	
	def 'Should be able to sort messages'() {
		given:
			createTestMessages()
		when:
			to PageMessageInbox
			messageListHeader.source.click()
		then:
			waitFor { getColumnText('main-list', 2) == ['Contact 1', 'Contact 2']}
		when:
			messageListHeader.message.click()
		then:
			getColumnText('main-list', 3) == ['An inbox message', 'Another inbox message']
	}

	def createTestMessages() {
		9.times {
			new Contact(name:"Contact ${it}", mobile:"123456789${it}").save(failOnError:true, flush:true)
		}
		Fmessage inboxMessage = new Fmessage(inbound:true, deleted:false, text:'An inbox message', src:'1234567891', dateCreated:new Date()-10).save(flush:true)
		Fmessage anotherInboxMessage = new Fmessage(inbound:true,deleted:false, text:'Another inbox message', src:'1234567892', dateCreated:new Date()-20).save(flush:true)
		
		Fmessage sentMessage = new Fmessage(hasSent:true, deleted:false, text:'A sent message',src:'1234567893').save(flush:true)
		Fmessage anotherSentMessage = new Fmessage(hasSent:true,deleted:false, text:'Another sent message',src:'1234567894').save(flush:true)
		Fmessage deletedSentMessage = new Fmessage(hasSent:true,deleted:true, text:'Deleted sent message',src:'1234567895').save(flush:true)
		
		Fmessage sentFailedMessage = new Fmessage(hasFailed:true, deleted:false, text:'A sent failed message',src:'1234567896').save(flush:true)
		Fmessage sentPendingMessage = new Fmessage(hasPending:true,deleted:false, text:'A pending message',src:'1234567897').save(flush:true)		
	}
	
	def createReadUnreadMessages() {
		new Fmessage(inbound:true, deleted:false, text:'A read message', read:true, src:'1234567898').save(flush:true)
		new Fmessage(inbound:true, deleted:false, text:'Another unread message', read:false, src:'1234567898').save(flush:true)
		new Fmessage(inbound:true, deleted:false, text:'An unread message', read:false, src:'1234567899').save(flush:true)
	}
}
