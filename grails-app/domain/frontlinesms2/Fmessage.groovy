package frontlinesms2

class Fmessage {
	String src
	String dst
	String text
	Date dateCreated
	boolean inbound
	boolean read
//	PollResponse activity

	static constraints = {
		src(nullable:true)
		dst(nullable:true)
		text(nullable:true)
//		activity(nullable:true)
	}

	def getDisplayText() {
		def p = PollResponse.withCriteria {
			messages {
				eq('id', this.id)
			}
		}

		p?.size()?"${p[0].value} (\"${this.text}\")":this.text
	}
}
