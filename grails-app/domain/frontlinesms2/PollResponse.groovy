package frontlinesms2

class PollResponse {
	static hasMany = [messages:Fmessage]

	String value
	static constraints = {
		value(blank: false, nullable: false, maxSize: 255)
	} // having a unique value here makes all reponses across all polls unique - not what we want right now

	String toString() {
		"I am a PollResponse and " +
				id?"my ID is ${id}":'I have not been saved in the database' +
				"my value is ${value}"
	}

	static mapping = {
            messages cascade:'all'
			messages sort:'dateCreated'
			messages sort:'dateRecieved'
    }
}
