package frontlinesms2

class RadioShow extends MessageOwner {
	String name

	static constraints = {
		name(blank: false, nullable: false)
	}

	def getShowMessages(isStarred = false, max = null, offset = null) {
		Fmessage.owned(isStarred, this).list(sort:'dateReceived', order:'desc', max:max, offset: offset)
	}

	def countMessages(isStarred = false) {
		Fmessage.owned(isStarred,this).count()
	}
}
