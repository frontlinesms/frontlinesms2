package frontlinesms2.connection

class NewSmslibConnectionPage extends geb.Page {
	static at = {
		assert title.endsWith('Configure new phone/modem connection')
		true
	}

	static content = {
		frmNewConnection { $('form.newConnection') }
		btnNewConnectionSave {frmNewConnection.find('.create')}
	}
}

