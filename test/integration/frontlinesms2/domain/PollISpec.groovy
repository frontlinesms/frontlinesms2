package frontlinesms2.domain

import frontlinesms2.*

class PollISpec extends grails.plugin.spock.IntegrationSpec {
	def 'Deleted messages do not show up as responses'() {
		when:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save()
			def message2 = new Fmessage(src:'Alice', dst:'+2541234567', text:'go barcelona', status:MessageStatus.INBOUND).save()
			def p = Poll.createPoll(title: 'This is a poll', choiceA: 'Manchester', choiceB:'Barcelona').save(failOnError:true, flush:true)
			PollResponse.findByValue('Manchester').addToMessages(message1).save(failOnError: true)
			PollResponse.findByValue('Barcelona').addToMessages(message2).save(failOnError: true)
			p.save(flush:true, failOnError:true)
		then:
			p.getPollMessages().count() == 2
		when:
			message1.toDelete().save(flush:true, failOnError:true)
		then:
			p.getPollMessages().count() == 1
	}

	def 'Response stats are calculated correctly, even when messages are deleted'() {
		given:
			def p = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris').save(failOnError:true, flush:true)
		when:
			def ukId = PollResponse.findByValue('Unknown').id
			def mjId = PollResponse.findByValue('Michael-Jackson').id
			def cnId = PollResponse.findByValue('Chuck-Norris').id
		then:
			p.responseStats == [
				[id:mjId, value:"Michael-Jackson", count:0, percent:0],
				[id:cnId, value:"Chuck-Norris", count:0, percent:0],
				[id:ukId, value:"Unknown", count:0, percent:0]
			]
		when:
			PollResponse.findByValue('Michael-Jackson').addToMessages(new Fmessage(text:'MJ').save(failOnError:true, flush:true))
			PollResponse.findByValue('Chuck-Norris').addToMessages(new Fmessage(text:'big charlie').save(failOnError:true, flush:true))
		then:
			p.responseStats == [
				[id:mjId, value:'Michael-Jackson', count:1, percent:50],
				[id:cnId, value:"Chuck-Norris", count:1, percent:50],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
		when:
			Fmessage.findByText('MJ').toDelete()
		then:
			p.responseStats == [
				[id:mjId, value:'Michael-Jackson', count:0, percent:0],
				[id:cnId, value:'Chuck-Norris', count:1, percent:100],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
	}

	def "creating a new poll also creates a poll response with value 'Unknown'"() {
		when:
			def p = Poll.createPoll(title: 'This is a poll', choiceA: 'one', choiceB:'two')
		then:
			p.responses.size() == 3
	}

    def "should sort messages based on date received"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByTitle('question').getPollMessages()
		then:
			results.list(sort:'dateReceived', order:'desc')*.src == ["src2", "src3", "src1"]
			results.list().every {it.archived == false}
    }

	def "should fetch starred poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByTitle("question").getPollMessages(['starred':true])
		then:
			results.list()*.src == ["src3"]
			results.list().every {it.archived == false}
	}

	def "should check for offset and limit while fetching poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByTitle("question").getPollMessages().list(max:1, offset:0)
		then:
			results*.src == ["src2"]
	}

	def "should return count of poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByTitle("question").getPollMessages().count()
		then:
			results == 3
	}
	
	def "title uniqueness should be case-insensitive"() {
		given:
			setUpPollResponseAndItsMessages()
		when:
			def poll = new Poll(title: 'Question')
			poll.addToResponses(new PollResponse(value: "response 1"))
			poll.addToResponses(new PollResponse(value: "response 2"))
		then:
			!poll.validate()
	}

	def "keyword should always be saved as uppercase"() {
		when:
			def p1 = setUpPollAndResponses()
			p1.keyword = 'tofu'
			p1.save(failOnError:true, flush:true)
		then:
			p1.keyword == 'TOFU'
		when:
			p1.keyword = 'tOFu'
			p1.save(failOnError:true, flush:true) // this is actually an UPDATE rather than a save
		then:
			p1.keyword == 'TOFU'
	}

	def "Poll keyword should be unique, ignoring case, among unarchived polls"() {
		given:
			def p1 = Poll.createPoll(keyword:'something', title:'p1', choiceA:'yes', choiceB:'no').save(failOnError:true, flush:true)
		when:
			def p2 = Poll.createPoll(keyword:'someTHING', title:'p2', choiceA:'yes', choiceB:'no')
		then:
			!p2.validate()
	}

	def "Poll keyword should not be unique between archived polls"() {
		given:
			def p1 = Poll.createPoll(keyword:'something', archived:true, title:'p1', choiceA:'yes', choiceB:'no').save(failOnError:true, flush:true)
		when:
			def p2 = Poll.createPoll(keyword:'someTHING', archived:true, title:'p2', choiceA:'yes', choiceB:'no')
		then:
			p2.validate()
	}

	def "Poll keyword in unarchived poll may be the same as that in an archived poll"() {
		given:
			def p1 = Poll.createPoll(keyword:'something', archived:true, title:'p1', choiceA:'yes', choiceB:'no').save(failOnError:true, flush:true)
		when:
			def p2 = Poll.createPoll(keyword:'someTHING', archived:false, title:'p2', choiceA:'yes', choiceB:'no')
		then:
			p2.validate()
	}
	
	private def setUpPollAndResponses() {		
		def poll = new Poll(title: 'question')
		poll.addToResponses(new PollResponse(value: "response 1"))
		poll.addToResponses(new PollResponse(value: "response 2"))
		poll.addToResponses(new PollResponse(value: "response 3"))
		poll.save(flush: true, failOnError:true)
		return poll
	}

	private def setUpPollResponseAndItsMessages() {
		def poll = setUpPollAndResponses()

		PollResponse.findByValue("response 1").addToMessages(new Fmessage(src: "src1", dateReceived: new Date() - 10)).save(flush: true, failOnError:true)
		PollResponse.findByValue("response 2").addToMessages(new Fmessage(src: "src2", dateReceived: new Date() - 2)).save(flush: true, failOnError:true)
		PollResponse.findByValue("response 3").addToMessages(new Fmessage(src: "src3", dateReceived: new Date() - 5, starred: true)).save(flush: true, failOnError:true)
	}
}

