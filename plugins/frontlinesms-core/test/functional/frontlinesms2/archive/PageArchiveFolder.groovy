package frontlinesms2.archive

class PageArchiveFolder extends PageArchive {
	static def url = "archive/folder"

	static at = {
		title.endsWith('Folder archive')
	}

	static content = {
		folderNames(required:false) { $(".folder-name-cell a") }
	}
}

