package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.utils.*

@Mixin(GebUtil)
class MessageListSpec extends grails.plugin.geb.GebSpec {

    def 'button to view inbox messages exists and goes to INBOX page'() {
        when:
            to PageMessageInbox
			def btnInbox = $('#messages-menu li a', href:"/frontlinesms2/message/inbox")
        then:
			btnInbox.text() == 'Inbox'
    }

    def 'button to view sent messages exists and goes to SENT page'() {
        when:
	        to PageMessageInbox
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
			to PageMessageInbox
		then:
		$('#messages-submenu li')*.text()[0] == 'Inbox'
		$('#messages-submenu li')*.text()[1] == 'Sent'
		$('#messages-submenu li')*.text()[2] == 'Pending'
		$('#messages-submenu li')*.text()[3] == 'Trash'
	}
	
	def 'Messages tab should have unread messages count next to it'() {
		given:
			createReadUnreadMessages()
		when:
			to PageMessageInbox
		then:
		$('#tab-messages').text() == 'Messages 2'
	}
	
	def 'Should be able to sort messages'() {
		given:
			createTestMessages()
		when:
			to PageMessageInbox
			$("#source-header a").click()
		then:
			getColumnAsArray($("table tr"), 2) == ['Contact 1', 'Contact 2']
		when:
			$("#message-header a").click()
		then:
			getColumnAsArray($("table tr"), 3) == ['An inbox message', 'Another inbox message']		
	}
	   
    def assertMenuItemSelected(String itemText) {
        def selectedChildren = $('#messages-menu li.selected')
        assert selectedChildren.size() == 1
        assert selectedChildren.text().contains(itemText)
        true
    }
	
	def createTestMessages() {
		9.times {
			new Contact(name:"Contact ${it}", primaryMobile:"123456789${it}").save(failOnError:true)
		}
		Fmessage inboxMessage = new Fmessage(status:MessageStatus.INBOUND, deleted:false, text:'An inbox message', src:'1234567891', dateCreated:new Date()-10).save(flush:true)
		Fmessage anotherInboxMessage = new Fmessage(status:MessageStatus.INBOUND,deleted:false, text:'Another inbox message', src:'1234567892', dateCreated:new Date()-20).save(flush:true)
		
		Fmessage sentMessage = new Fmessage(status:MessageStatus.SENT, deleted:false, text:'A sent message',src:'1234567893').save(flush:true)
		Fmessage anotherSentMessage = new Fmessage(status:MessageStatus.SENT,deleted:false, text:'Another sent message',src:'1234567894').save(flush:true)
		Fmessage deletedSentMessage = new Fmessage(status:MessageStatus.SENT,deleted:true, text:'Deleted sent message',src:'1234567895').save(flush:true)
		
		Fmessage sentFailedMessage = new Fmessage(status:MessageStatus.SEND_FAILED, deleted:false, text:'A sent failed message',src:'1234567896').save(flush:true)
		Fmessage sentPendingMessage = new Fmessage(status:MessageStatus.SEND_PENDING,deleted:false, text:'A pending message',src:'1234567897').save(flush:true)		
	}
	
	def createReadUnreadMessages() {
		new Fmessage(status:MessageStatus.INBOUND, deleted:false, text:'A read message', read:true, src:'1234567898').save(flush:true)
		new Fmessage(status:MessageStatus.INBOUND, deleted:false, text:'Another unread message', read:false, src:'1234567898').save(flush:true)
		new Fmessage(status:MessageStatus.INBOUND, deleted:false, text:'An unread message', read:false, src:'1234567899').save(flush:true)
	}
}
