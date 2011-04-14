package frontlinesms2

class ContactListPage extends geb.Page {
	static url = 'contact/list'
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
	}
}
