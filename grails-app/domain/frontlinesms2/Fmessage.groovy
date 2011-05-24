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

	static def getInboxMessages() {
		def list = Fmessage.findAll()
		def inboundList = []
		for (m in list) {
			if(m.inbound == true)
				inboundList.add(m)
		}
		inboundList
	}
	static def getSentMessages() {
		def list = Fmessage.findAll()
		def outBoundList = []
		for (m in list) {
			if(m.inbound == false)
				outBoundList.add(m)
		}
		outBoundList
	}
}
