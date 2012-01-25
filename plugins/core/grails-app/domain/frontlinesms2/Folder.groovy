package frontlinesms2

import java.util.Date

class Folder extends MessageOwner {
	static transients = ['liveMessageCount']
	
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}
	
	def getFolderMessages(getOnlyStarred = false) {
		Fmessage.owned(getOnlyStarred, this)
	}
}
