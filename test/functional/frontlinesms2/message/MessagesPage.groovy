package frontlinesms2.message

class MessagesPage extends geb.Page {
	static url = 'message/inbox'
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
	}
}

