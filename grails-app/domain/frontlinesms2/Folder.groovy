package frontlinesms2

import java.util.Date;

class Folder extends MessageOwner {
	static transients = ['liveMessageCount']
	String name
	Date dateCreated
	boolean archived
	
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}
	
	def getFolderMessages(getOnlyStarred = false) {
		Fmessage.owned(getOnlyStarred, this)
	}
	
	def setArchivedProperty(boolean archived) {
		this.archived = archived
		def messagesToArchive = Fmessage?.owned(this)?.list()
		messagesToArchive.each { it?.archived = archived }
	}
	
	def unarchiveFolder() {
		this.archived = false
		def messagesToUnarchive = Fmessage.owned(this)?.list()
		messagesToUnarchive.each { it?.archived = false }
	}
		
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndDeleted(this, false)
		m ? m.size() : 0
	}
}
