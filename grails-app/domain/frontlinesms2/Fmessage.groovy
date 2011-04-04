package frontlinesms2

class Fmessage {
	String src
	String dst
	String text

	static constraints = {
		src(nullable:true)
		dst(nullable: true)
		text(nullable: true)
	}
}
