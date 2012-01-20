package frontlinesms2

import java.util.Date;

class Announcement extends MessageOwner {
	static transients = ['liveMessageCount']
	String name
	
	static constraints = {
		name(nullable:false)
	}
	
	def getAnnouncementMessages(getOnlyStarred = false) {
		Fmessage.owned(getOnlyStarred, this)
	}
	
}
