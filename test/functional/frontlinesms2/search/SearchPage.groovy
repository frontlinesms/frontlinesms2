package frontlinesms2.search

class SearchPage extends geb.Page {
	static url = 'search/result?searchString=hi'
	static at = {
		title.startsWith("Search")
	}
	static content = {
		messagesSelect { $(".message-select") }
		checkedMessageCountText { $("#checked-message-count").text() }
		archiveAllButton { $('#multiple-messages #btn_archive_all') }
		multipleMessagesPanel { $('#multiple-messages') }
		replyToMultipleButton { $('#multiple-messages a')[0] }
	}
}

