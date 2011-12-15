package frontlinesms2

import java.util.Date;

class Announcement extends MessageOwner {
	static transients = ['liveMessageCount']
	String name
	String sentMessage
	
	static constraints = {
		name(nullable:false)
		sentMessage(nullable:false, blank: false)
	}
	
	def getAnnouncementMessages(getOnlyStarred = false) {
		Fmessage.owned(getOnlyStarred, this)
	}
}
