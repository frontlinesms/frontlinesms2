package frontlinesms2.archive

class PageArchive extends geb.Page {
	static def url = "archive"	
	static at = {
		title.endsWith('Inbox archive')
	}
}