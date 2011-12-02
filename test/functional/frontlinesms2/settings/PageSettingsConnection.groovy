package frontlinesms2.settings

import frontlinesms2.*

class ConnectionPage extends geb.Page {
	static url = 'settings/connections'
	static content = {
		btnNewConnection { $('#create-connection-btn a') }
		connectionNames { $('connection-header h2') }
	}
}