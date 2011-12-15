package frontlinesms2.message

class PageMessageTrash extends PageMessage {
	static url = 'message/trash'
	static at = {
		title.endsWith('Trash')
	}
}