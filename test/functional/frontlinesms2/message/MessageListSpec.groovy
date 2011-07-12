package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class MessageListSpec extends grails.plugin.geb.GebSpec {
    
    def 'button to view inbox messages exists and goes to INBOX page'() {
        when:
            to MessagesPage
			def btnInbox = $('#messages-menu li a', href:"/frontlinesms2/message/inbox")
        then:
			btnInbox.text() == 'Inbox'
    }

    def 'button to view sent messages exists and goes to SENT page'() {
        when:
	        to MessagesPage
			def btnSentItems = $('#messages-menu li a', href:'/frontlinesms2/message/sent')
        then:
			btnSentItems.text() == 'Sent'
    }
    
    def 'when in inbox, Inbox menu item is highlighted'() {
        when:
            go "message"
        then:
            assertMenuItemSelected("Inbox")
	}
	
	def 'when viewing Sent Items, Sent Items menu item is hilighted'() {
        when:
            go "message/sent"
        then:
            assertMenuItemSelected("Sent")
    }

	def 'Messages should list count of messages next to them'() {
		given:
			createTestMessages()
		when:
			to MessagesPage
		then:
		$('#messages-submenu li')*.text() == ['Inbox (2)', 'Pending (2)', 'Sent (2)', 'Trash (1)']
		cleanup:
			deleteTestMessages()
	}
    
    def assertMenuItemSelected(String itemText) {
        def selectedChildren = $('#messages-menu li.selected')
        assert selectedChildren.size() == 1
        assert selectedChildren.text().contains(itemText)
        true
    }
	
	def createTestMessages() {
		Fmessage inboxMessage = new Fmessage(status:MessageStatus.INBOUND, deleted:false, text:'An inbox message').save(flush:true)
		Fmessage anotherInboxMessage = new Fmessage(status:MessageStatus.INBOUND,deleted:false, text:'Another inbox message').save(flush:true)
		
		Fmessage sentMessage = new Fmessage(status:MessageStatus.SENT, deleted:false, text:'A sent message').save(flush:true)
		Fmessage anotherSentMessage = new Fmessage(status:MessageStatus.SENT,deleted:false, text:'Another sent message').save(flush:true)
		Fmessage deletedSentMessage = new Fmessage(status:MessageStatus.SENT,deleted:true, text:'Deleted sent message').save(flush:true)
		
		Fmessage sentFailedMessage = new Fmessage(status:MessageStatus.SEND_FAILED, deleted:false, text:'A sent failed message').save(flush:true)
		Fmessage sentPendingMessage = new Fmessage(status:MessageStatus.SEND_PENDING,deleted:false, text:'A pending message').save(flush:true)		
	}
	
	def deleteTestMessages() {
		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
	
}
