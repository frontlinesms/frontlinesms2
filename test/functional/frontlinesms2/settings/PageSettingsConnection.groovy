package frontlinesms2.settings

import frontlinesms2.*

class ConnectionPage extends geb.Page {
	static url = 'settings/connections'
	static content = {
		btnNewConnection { $('#btnNewConnection a') }
		connectionNames { $('.con-name') }
	}
}