package frontlinesms2

class PollSpec extends grails.plugin.spock.UnitSpec {
	/** some responses that should pass validation */
	def OK_RESPONSES = [new PollResponse(value: "one"), new PollResponse(value: "two")]
	
	def setup() {
		registerMetaClass Poll
		Poll.metaClass.static.withCriteria = { null } // this allows validation of 'title' field to pass
		Poll.metaClass.static.findByTitleIlike = { null }
	}
	
	def 'Poll must have at least two responses'() {
		given:
			mockDomain(Poll)
		when:
			def p = new Poll(title:'test poll')
		then:
			!p.validate()
		when:
			p.addToResponses(new PollResponse(value:'one'))
		then:
			!p.validate()
		when:
			p.addToResponses(new PollResponse(value:'two'))
		then:
			p.validate()
		when:
			p.addToResponses(new PollResponse(value:'three'))
		then:
			p.validate()
	}

	def "poll auto-reply cannot be blank"() {
		setup:
			mockDomain(Poll)
		when:
			def poll = new Poll(title:"title", autoReplyText:" ", responses:OK_RESPONSES)
		then:
			!poll.validate()
	}
	
	def "Poll keyword may not contain whitespace"() {
		given:
			mockForConstraintsTests(Poll)
		when:
			def p = new Poll(keyword:'with space', title:'test', responses:OK_RESPONSES)
		then:
			!p.validate()
			p.errors.keyword
	}
	
	def "beforeSave should convert keyword to upper case"() {
		given:
			Poll p = new Poll(keyword:'shoehorn')
		when:
			p.beforeSave()
		then:
			p.keyword == 'SHOEHORN'
	}

	def "beforeSave should convert empty keyword to null"() {
		given:
			Poll p = new Poll(keyword:' ')
		when:
			p.beforeSave()
		then:
			p.keyword == null
	}

	def "beforeSave should leave null keyword intact"() {
		given:
			Poll p = new Poll(keyword:null)
		when:
			p.beforeSave()
		then:
			p.keyword == null
	}

    def "toDelete should change deleted flag to true"() {
        given:
			Poll p = new Poll(keyword:null)
		when:
            assert !p.deleted
			p.toDelete()
		then:
			p.deleted
    }
}
