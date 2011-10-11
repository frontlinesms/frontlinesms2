package frontlinesms2.search

class PageSearchResultHi extends PageSearch {
	static url = 'search/result?searchString=hi'
	static at = {
		title.startsWith("Search")
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
	}
}

