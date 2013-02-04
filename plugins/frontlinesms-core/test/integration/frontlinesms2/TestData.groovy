package frontlinesms2

class TestData {
	static void createMiaowMixPoll(def baseDate=new Date(), int liverMessageCount=1) {
		def chickenMessage = Fmessage.build(src:'Barnabus', text:'i like chicken', date:baseDate)
				.save(failOnError:true, flush:true)
		def liverMessage = Fmessage.build(src:'Minime', text:'i like liver', date:baseDate)
				.save(failOnError:true, flush:true)
		def chickenResponse = new PollResponse(key:'A', value:'chicken')
		def liverResponse = new PollResponse(key:'B', value:'liver')
		def poll = new Poll(name:'Miauow Mix')
				.addToResponses(chickenResponse)
				.addToResponses(liverResponse)
				.addToResponses(PollResponse.createUnknown())
				.save(failOnError:true, flush:true)
		liverResponse.addToMessages(liverMessage)
		if(liverMessageCount > 1) {
			def liverMessage2 = Fmessage.build(src:'+254333222', text:'liver for lunch?', date:baseDate)
					.save(failOnError:true, flush:true)
			liverResponse.addToMessages(liverMessage2)
		}
		chickenResponse.addToMessages(chickenMessage)
		poll.save(failOnError:true, flush:true)
	}

	static createFootballPoll() {
		def p = new Poll(name: 'This is a poll', yesNo:false)
		p.addToResponses(key:'A', value:"Manchester")
		p.addToResponses(key:'B', value:"Barcelona")
		p.addToResponses(key:'C', value:"Harambee Stars")
		p.addToResponses(PollResponse.createUnknown())
		p.save(failOnError:true)
		return p
	}

	static createFootballPollWithKeywords() {
		def p = createFootballPoll()
		def k1 = new Keyword(value: "FOOTBALL", activity: p)
		def k2 = new Keyword(value: "MANCHESTER", activity: p, ownerDetail:"A", isTopLevel:false)
		def k3 = new Keyword(value: "HARAMBEE", activity: p, ownerDetail:"C", isTopLevel:false)
		def k4 = new Keyword(value: "BARCELONA", activity: p, ownerDetail:"B", isTopLevel:false)
		p.addToKeywords(k1)
		p.addToKeywords(k2)
		p.addToKeywords(k3)
		p.addToKeywords(k4)
		p.save(failOnError:true, flush:true)
		return p
	}

	static createBadnessPoll() {
		def poll = new Poll(name: 'Who is badder?', question: "question", autoreplyText: "Thanks")
		poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
		poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
		poll.addToResponses(PollResponse.createUnknown())
		poll.save(failOnError:true, flush:true)
		return poll
	}
}

