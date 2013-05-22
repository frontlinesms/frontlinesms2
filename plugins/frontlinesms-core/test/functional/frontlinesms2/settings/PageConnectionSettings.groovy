package frontlinesms2.settings

import frontlinesms2.*

class PageConnectionSettings extends PageSettings {
	static url = 'connection/list'
	static at = {
		title.contains('Connections')
	}
	
	static content = {
		btnNewConnection { $('a', name:'addConnection') }
		connectionNames { $('.connection-header').find('h2') }
		connections { $('.connection') }
		routing { module RoutingConnections }
	}
}

class RoutingConnections extends geb.Module {
	static base = { $('#routing-preferences') }

	static content = {
		routingForm { $('form#routing-form') }
		useLastReceivedConnection { $('input[type=checkbox]', value:'uselastreceiver') }
		useAnyAvailableConnection { $('input[name=otherwise]', value:'any') }
		dontSend { $('input[name=otherwise]', value:'dontsend') }
		save { $("input#saveRoutingDetails") }
	}
}

