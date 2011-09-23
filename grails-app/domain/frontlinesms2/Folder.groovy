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
	
	def archiveFolder() {
		this.archived = true
		def messagesToArchive = Fmessage.owned(this).list()
		messagesToArchive.each { it.archived = true }
	}
	
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndDeleted(this, false)
		m ? m.size() : 0
	}
}