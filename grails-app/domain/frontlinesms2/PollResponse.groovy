package frontlinesms2

class PollResponse {
//	static belongsTo = [poll:Poll]
	static hasMany = [messages:Fmessage]

	String value
	static constraints = {
		value(unique: true, blank: false, nullable: false, maxSize: 255)
	}

	String toString() {
		"I am a PollResponse and " +
				id?"my ID is ${id}":'I have not been saved in the database' +
				"my value is ${value}"
	}

	static mapping = {
            messages cascade:'all'
    }
}
