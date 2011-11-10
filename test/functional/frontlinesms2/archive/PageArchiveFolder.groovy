package frontlinesms2.archive

class PageArchiveFolder extends PageArchive {
	static def url = "archive/folder"
	
	static at = {
		title.endsWith('Archive')
	}
	
	static content = {
		folderNames { $(".folder-name") }
	}
}