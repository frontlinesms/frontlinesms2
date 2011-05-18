package frontlinesms2

class Fmessage {
//	static belongsTo = [activity : Poll]

	String src
	String dst
	String text
	Date dateCreated
	boolean inbound
	boolean read

	static constraints = {
		src(nullable:true)
		dst(nullable:true)
		text(nullable:true)
	}
}
