package frontlinesms2.message

abstract class PageMessage extends geb.Page {
	static url = 'message/'
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
		createActivityButton { $("#create-activity a") }
		createActivityDialog(required:false) { $("#ui-dialog-title-modalBox") }
		archiveBtn(required:false){$("#message-detail #archive-msg")}
		deleteBtn(required:false) {$("#message-detail #delete-msg")}
	}
	
}