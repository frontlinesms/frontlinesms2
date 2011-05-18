package frontlinesms2

class Poll {
	String title
	static hasMany = [responses : String]
//	static hasMany = [messages : Fmessage]

	static constraints = {
		title(unique: true, blank: false, nullable: false, maxSize: 255)
//		responses(unique: true, blank: false, nullable: false, minSize: 2)
	}
}

