package frontlinesms2.archive

class ArchiveFolderPage extends geb.Page {
	static def url = "archive/folder"
	
	static at = {
		title.endsWith('Archive')
	}
	
	static content = {
		FolderTable { $('#folder-table') }
		FolderNames { $(".folder-name") }
	}
}