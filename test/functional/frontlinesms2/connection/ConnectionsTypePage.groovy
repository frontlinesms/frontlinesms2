package frontlinesms2.connection

class ConnectionsTypePage extends geb.Page {
	static url = 'connection/create'
	static content = {
		lstConnectionTypes { $('#connectionTypes') }
		btnNewEmailConnection { lstConnectionTypes.find('.email') }
		btnNewSmslibConnection { lstConnectionTypes.find('.smslib') }
	}
}
