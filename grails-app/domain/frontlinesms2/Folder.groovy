package frontlinesms2

class Folder extends MessageOwner {
	static transients = ['folderMessages']
	String name
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}
	
	def getFolderMessages() { // FIXME this should be done just using folder.messages and having sort and deleted filter applied automatically
		Fmessage.createCriteria().list {
			and {
				eq("deleted", false)
				eq("messageOwner", this)
			}
			order('dateReceived', 'desc')
		}
	}
	
}