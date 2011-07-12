package frontlinesms2

class Folder extends MessageOwner {
	static transients = ['folderMessages']
	String name
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}
	
	def getFolderMessages(isStarred) { // FIXME this should be done just using folder.messages and having sort and deleted filter applied automatically
		Fmessage.owned(isStarred, this).list(sort:'dateReceived', order:'desc')
	}
	
	def countMessages(isStarred) {
		Fmessage.owned(isStarred,this).count()
	}
	
}