package frontlinesms2.base

import frontlinesms2.*

abstract class PageBase extends geb.Page {
	static content = {
		tabs { module TabsModule }
		notifications { module NotificationsModule }
	}
}

class TabsModule extends geb.Module {
	static content = {
		current {
			($('#main-nav li.current a').text() 
			- $('#main-nav li.current a #inbox-indicator').text()).trim().toLowerCase()
		}
		unreadcount {
			$('#inbox-indicator').text().toInteger()
		}
		status {
			$('#inbox-indicator').classes().find { it in ['red', 'green', 'orange'] }
		}
	}
}

class NotificationsModule extends geb.Module {
	static content = { 
		errorMessages { $('.flash.errors')*.text() }
		flashMessages { $('div.flash.message').text() }
	}
}