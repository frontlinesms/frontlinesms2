package frontlinesms2

class RadioShow extends MessageOwner {
	String name

	static constraints = {
		name(blank: false, nullable: false)
	}

	def getShowMessages(params) {
		Fmessage.owned(params['starred'], this).list(params)
	}

	def countMessages(isStarred = false) {                                                                                       
		Fmessage.owned(isStarred,this).count()
	}
}
