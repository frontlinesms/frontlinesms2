package frontlinesms2.search

class PageSearchResult extends PageSearch {
	static url = 'search/result'
	static at = {
		title.startsWith("Results")
	}
	static content = {
		messagesSelect { $(".message-select") }
		archiveAllButton { $('#multiple-messages #btn_archive_all') }
		multipleMessagesPanel { $('#multiple-messages') }
		replyToMultipleButton { $('#multiple-messages a')[0] }
		checkedMessageCount {
			def t = $("#checked-message-count").text()
			if(t != null) {
				return t - ' messages selected' as Integer
			} else {
				return $('.message-select:checked').size()
			}
		}
		messageList {$("#messages tbody tr")}
	}
}