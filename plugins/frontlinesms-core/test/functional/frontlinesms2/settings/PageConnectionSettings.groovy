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
	}
}
