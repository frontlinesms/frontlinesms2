package frontlinesms2

class Folder extends MessageOwner {
	static transients = ['folderMessages']
	String name
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}
	
	def getFolderMessages(params) {
		Fmessage.owned(params['starred'], this).list(params)
	}

	def countMessages(isStarred) {
		Fmessage.owned(isStarred,this).count()
	}
	
}