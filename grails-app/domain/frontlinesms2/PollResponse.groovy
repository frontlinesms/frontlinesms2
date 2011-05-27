package frontlinesms2

class PollResponse {
	static hasMany = [messages:Fmessage]

	String value
	static constraints = {
		value(unique: true, blank: false, nullable: false, maxSize: 255)
	}

	static mapping = {
		messages cascade:'all'
		messages sort:'dateCreated'
		messages sort:'dateRecieved'
	}
}
