package frontlinesms2.message

class MessagesPage extends geb.Page {
	static url = 'message/inbox'
//	static at = {
//		title.endsWith('Inbox')
//	}
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
	}
}

