package frontlinesms2.message

import frontlinesms2.*

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
    
    def assertMenuItemSelected(String itemText) {
        def selectedChildren = $('#messages-menu li.selected')
        assert selectedChildren.size() == 1
        assert selectedChildren.text() == itemText
        true
    }
}
