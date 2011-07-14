package frontlinesms2

class Folder extends MessageOwner {
	static transients = ['folderMessages']
	String name
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}
	
	def getFolderMessages(isStarred, max, offset) {
		Fmessage.owned(isStarred, this).list(sort:'dateReceived', order:'desc', max:max, offset: offset)
	}
	
	def getFolderMessages(isStarred) {
		getFolderMessages(isStarred, null, null)
	}

	def countMessages(isStarred) {
		Fmessage.owned(isStarred,this).count()
	}
	
}