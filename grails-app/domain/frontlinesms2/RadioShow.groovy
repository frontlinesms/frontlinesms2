package frontlinesms2

class RadioShow extends MessageOwner {
	String name

	static constraints = {
		name(blank: false, nullable: false, unique: true, validator: { val, obj ->
			RadioShow.findByNameIlike(val) == null
		})
	}

	def getShowMessages(getOnlyStarred = false) {
		Fmessage.owned(getOnlyStarred, this)
	}
}
