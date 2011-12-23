package frontlinesms2.radio

import java.util.Date
import frontlinesms2.*

class RadioShow extends MessageOwner {
	String name
	Date dateCreated

	static constraints = {
		name(blank: false, nullable: false, unique: true)
	}

	def getShowMessages(getOnlyStarred = false) {
		Fmessage.owned(getOnlyStarred, this)
	}
}
