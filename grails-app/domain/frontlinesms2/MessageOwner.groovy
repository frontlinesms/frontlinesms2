package frontlinesms2

class MessageOwner {
	static hasMany = [messages:Fmessage]
	String value
	static constraints = {
		value(blank: false, nullable: false, maxSize: 255)
	} // having a unique value here makes all reponses across all polls unique - not what we want right now

	static mapping = {
		messages cascade:'all'
		messages sort:'dateCreated'
		messages sort:'dateReceived'
	}
}