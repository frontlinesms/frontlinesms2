package frontlinesms2.message

class MessagesPage extends geb.Page {
	static url = 'message/inbox'
	static content = {
		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages-submenu') }
		messagesSelect { $(".message-select") }
		flashMessage(required:false) { $("div.flash") }
		multipleMessagesThing(required:false) { $('#multiple-messages') }
		deleteAllButton(required:false) { $("#btn_delete_all") }
		checkedMessageCount { 
			def t = $("#checked-message-count").text()
			if(t != null) {
				return t - ' messages selected' as Integer
			} else {
				return $('.message-select:checked').size()
			}
		}
	}
}

