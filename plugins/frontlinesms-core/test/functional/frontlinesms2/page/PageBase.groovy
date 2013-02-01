package frontlinesms2.page

import frontlinesms2.*
import geb.Module

abstract class PageBase extends geb.Page {
	static content = {
		systemMenu { module SystemMenu }
		tabs { module TabsModule }
		notifications { module NotificationsModule }
	}
}

class SystemMenu extends Module {
	static base = { $("#system-menu") }
	static content = {
		help { $('a', controller:'help') }
	}
}

class TabsModule extends Module {
	static content = {
		current {
			($('#main-nav li.current a').text() 
			- $('#main-nav li.current a #inbox-indicator').text()).trim().toLowerCase()
		}
		unreadcount {
			$('#inbox-indicator').text()?.toInteger()
		}
		status {
			$('#inbox-indicator').classes().find { it in ['red', 'green', 'orange'] }
		}
	}
}

class NotificationsModule extends Module {
	static content = { 
		errorMessages { $('.flash.errors')*.text() }
		flashMessageText { def t = flashMessage.text(); t&&t.size()>1? t[0..-2].trim(): '' }
		flashMessage { $('div.flash.message') }
		systemNotification { $('div.system-notification') }
		systemNotificationText { systemNotification.text() }
	}
}
