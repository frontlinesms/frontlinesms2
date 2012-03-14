package frontlinesms2.domain

import frontlinesms2.*

class PollISpec extends grails.plugin.spock.IntegrationSpec {

	def 'Deleted messages do not show up as responses'() {
		when:
			def message1 = new Fmessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save()
			def message2 = new Fmessage(src:'Alice', text:'go barcelona', inbound:true, date: new Date()).save()
			def p = Poll.createPoll(name: 'This is a poll', choiceA: 'Manchester', choiceB:'Barcelona').save(failOnError:true, flush:true)
			PollResponse.findByValue('Manchester').addToMessages(message1)
			PollResponse.findByValue('Barcelona').addToMessages(message2)
			p.save(flush:true, failOnError:true)
		then:
			p.getActivityMessages().count() == 2
		when:
			message1.isDeleted = true
			message1.save(flush:true, failOnError:true)
		then:
			p.getActivityMessages().count() == 1
	}

	def 'Response stats are calculated correctly, even when messages are deleted'() {
		given:
			def p = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris').save(failOnError:true, flush:true)
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
			PollResponse.findByValue('Michael-Jackson').addToMessages(new Fmessage(text:'MJ', date: new Date(), inbound: true, src: '12345').save(failOnError:true, flush:true))
			PollResponse.findByValue('Chuck-Norris').addToMessages(new Fmessage(text:'big charlie', date: new Date(), inbound: true, src: '12345').save(failOnError:true, flush:true))
		then:
			p.responseStats == [
				[id:mjId, value:'Michael-Jackson', count:1, percent:50],
				[id:cnId, value:"Chuck-Norris", count:1, percent:50],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
		when:
			Fmessage.findByText('MJ').isDeleted = true
			Fmessage.findByText('MJ').save(flush:true)
		then:
			p.responseStats == [
				[id:mjId, value:'Michael-Jackson', count:0, percent:0],
				[id:cnId, value:'Chuck-Norris', count:1, percent:100],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
	}

	def "creating a new poll also creates a poll response with value 'Unknown'"() {
		when:
			def p = Poll.createPoll(name: 'This is a poll', choiceA: 'one', choiceB:'two')
		then:
			p.responses.size() == 3
	}

    def "should sort messages based on date"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByName('question').getActivityMessages()
		then:
			results.list(sort:'date', order:'desc')*.src == ["src2", "src3", "src1"]
			results.list().every {it.archived == false}
    }

	def "should fetch starred poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByName("question").getActivityMessages(true)
		then:
			results.list()*.src == ["src3"]
			results.list().every {it.archived == false}
	}

	def "should check for offset and limit while fetching poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByName("question").getActivityMessages().list(max:1, offset:0)
		then:
			results*.src == ["src2"]
	}

	def "should return count of poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByName("question").getActivityMessages().count()
		then:
			results == 3
	}
	
	def "name uniqueness should be case-insensitive"() {
		given:
			setUpPollResponseAndItsMessages()
		when:
			def poll = new Poll(name: 'Question')
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
			def p1 = Poll.createPoll(keyword:'something', name:'p1', choiceA:'yes', choiceB:'no').save(failOnError:true, flush:true)
		when:
			def p2 = new Poll(keyword:'someTHING', name:'p2', choiceA:'yes', choiceB:'no')
		then:
			!p2.validate()
	}

	def "Poll keyword should not be unique between archived polls"() {
		given:
			def p1 = Poll.createPoll(keyword:'something', archived:true, name:'p1', choiceA:'yes', choiceB:'no').save(failOnError:true, flush:true)
		when:
			def p2 = Poll.createPoll(keyword:'someTHING', archived:true, name:'p2', choiceA:'yes', choiceB:'no')
		then:
			p2.validate()
	}

	def "Poll keyword in unarchived poll may be the same as that in an archived poll"() {
		given:
			def p1 = Poll.createPoll(keyword:'something', archived:true, name:'p1', choiceA:'yes', choiceB:'no').save(failOnError:true, flush:true)
		when:
			def p2 = Poll.createPoll(keyword:'someTHING', archived:false, name:'p2', choiceA:'yes', choiceB:'no')
		then:
			p2.validate()
	}
	
	def "can edit responses for a poll with multiple responses"() {
		when:
			def poll = Poll.createPoll([choiceA: "one", choiceB: "two", name:"title"])
		then:
			poll.responses*.value.containsAll(['one', 'two'])
		when:
			Poll.editPoll(poll.id, [choiceC: "three", choiceD:"four"])
			poll = Poll.get(poll.id)
		then:
			println "${poll.responses*.key}"
			poll.responses*.value.containsAll(['one', 'two', 'three', 'four', 'Unknown'])
		when:
			def m1 = new Fmessage(src: "src1", inbound: true, date: new Date() - 10)
			PollResponse.findByValue("one").addToMessages(m1)
			poll.save(flush:true)
		then:
			poll.liveMessageCount == 1
		when:
			Poll.editPoll(poll.id, [choiceA: "five"])
		then:
			poll.liveMessageCount == 1
			!PollResponse.findByValue("one")
			poll.responses.find {
				if(it.value == "Unknown") {
					it.messages.size() == 1
				}
			}
	}
	
	def "adding responses to a poll with multiple responses does not affect categorized messages"() {
		when:
			def poll = Poll.createPoll([choiceA: "one", choiceB: "two", name:"title"])
			def m1 = new Fmessage(src: "src1", inbound: true, date: new Date() - 10)
			def m2 = new Fmessage(src: "src2", inbound: true, date: new Date() - 10)
			def m3 = new Fmessage(src: "src3", inbound: true, date: new Date() - 10)
			PollResponse.findByValue("one").addToMessages(m1)
			PollResponse.findByValue("one").addToMessages(m2)
			PollResponse.findByValue("two").addToMessages(m3)
			poll.save(flush:true)
			poll.refresh()
		then:
			poll.responses*.liveMessageCount == [2, 1, 0]
		when:
			Poll.editPoll(poll.id, [choiceC: "three", choiceD:"four"])
			poll = Poll.get(poll.id)
		then:
			poll.responses*.value.containsAll(['one', 'two', 'three', 'four', 'Unknown'])
			poll.responses*.liveMessageCount == [2, 1, 0, 0, 0]
		when:
			m1 = new Fmessage(src: "src1", inbound: true, date: new Date() - 10)
			PollResponse.findByValue("one").addToMessages(m1)
			PollResponse.findByValue("three").addToMessages(new Fmessage(src: "src4", inbound: true, date: new Date() - 10))
			poll.save(flush:true)
			poll.refresh()
		then:
			poll.responses*.liveMessageCount == [3, 1, 0, 1, 0]
		when:
			Poll.editPoll(poll.id, [choiceA: "five"])
			poll.refresh()
		then:
			!PollResponse.findByValue("one")
			println "poll responses ${poll.responses*.value}"
			poll.responses*.findAll {
				if(it.key == 'Unknown') it.liveMessageCount == 3
				if(it.key == 'choiceA') it.liveMessageCount == 0
			}
	}
	
	def "Archiving a poll archives messages associated with the poll"(){
		given:
			def poll = Poll.createPoll(name: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris').save(failOnError:true, flush:true)
			def message1 = new Fmessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save()
			def message2 = new Fmessage(src:'Alice', text:'go barcelona', inbound:true, date: new Date()).save()
			poll.addToMessages(message1)
			poll.addToMessages(message2)
			poll.save(flush:true, failOnError:true)
		when:
			poll.archive()
			poll.refresh()
		then:
			poll.liveMessageCount == 2
			poll.archived
			poll.activityMessages.list().findAll {it.archived == true}
	}
	
	private def setUpPollAndResponses() {		
		def poll = new Poll(name: 'question')
		poll.addToResponses(new PollResponse(value: 'Unknown', key: 'Unknown'))
		poll.addToResponses(new PollResponse(value: "response 1"))
		poll.addToResponses(new PollResponse(value: "response 2"))
		poll.addToResponses(new PollResponse(value: "response 3"))
		poll.save(flush: true, failOnError:true)
		return poll
	}

	private def setUpPollResponseAndItsMessages() {
		def poll = setUpPollAndResponses()
		def m1 = new Fmessage(src: "src1", inbound: true, date: new Date() - 10)
		def m2 = new Fmessage(src: "src2", inbound: true, date: new Date() - 2)
		def m3 = new Fmessage(src: "src3", inbound: true, date: new Date() - 5, starred: true)
		PollResponse.findByValue("response 1").addToMessages(m1)
		PollResponse.findByValue("response 2").addToMessages(m2)
		PollResponse.findByValue("response 3").addToMessages(m3)
		poll.save(flush:true, failOnError:true)
	}

	def 'Adding a message will propogate it to the Unknown response'() {
		given:
			Poll p = setUpPollAndResponses()
			Fmessage m = new Fmessage(date:new Date(), inbound:true, src:"a-unit-test!").save(flush:true, failOnError:true)
			p.refresh()
			m.refresh()
			p.responses*.refresh()
			println "p.responses: $p.responses"
						println "p.responses.messages: $p.responses.messages"
		when:
			println "p.responses*.value: ${p.responses*.value}"
			println "p.responses.find { it.value == 'Unknown' }: ${p.responses.find { it.value == 'Unknown' }}"
			p.addToMessages(m)
			p.save(failOnError:true, flush:true)
		then:
			p.refresh()
			m.refresh()
			p.responses*.refresh()
			p.messages*.id == [m.id]
			p.responses.find { it.value == 'Unknown' }.messages*.id == [m.id]
			p.messages*.id == [m.id]
	}
}

