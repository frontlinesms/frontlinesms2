package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*

class PollISpec extends grails.plugin.spock.IntegrationSpec {

	def 'Deleted messages do not show up as responses'() {
		when:
			def message1 = new TextMessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save()
			def message2 = new TextMessage(src:'Alice', text:'go barcelona', inbound:true, date: new Date()).save()
			def p = new Poll(name: 'This is a poll')
			p.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
			p.save(failOnError:true, flush:true)
			PollResponse.findByValue('Manchester').addToMessages(message1)
			PollResponse.findByValue('Barcelona').addToMessages(message2)
			p.save(flush:true, failOnError:true)
		then:
			p.getActivityMessages().size() == 2
		when:
			message1.isDeleted = true
			message1.save(flush:true, failOnError:true)
		then:
			p.getActivityMessages().size() == 1
	}

	def 'Response stats are calculated correctly, even when messages are deleted and there are outbound messages'() {
		given:
			def p = new Poll(name: 'Who is badder?')
			p.editResponses(choiceA:'Michael-Jackson', choiceB:'Chuck-Norris')
			p.save(failOnError:true, flush:true)
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
			def outbound1 = new TextMessage(inbound:false, text:'who is badder in your opinion?')
			outbound1.addToDispatches(dst:"123", status:DispatchStatus.SENT, dateSent:new Date())
			p.addToMessages(outbound1)
			p.save(failOnError:true, flush:true)
		then:
			p.responseStats == [
				[id:mjId, value:"Michael-Jackson", count:0, percent:0],
				[id:cnId, value:"Chuck-Norris", count:0, percent:0],
				[id:ukId, value:"Unknown", count:0, percent:0]
			]
		when:
			PollResponse.findByValue('Michael-Jackson').addToMessages(new TextMessage(text:'MJ', date: new Date(), inbound: true, src: '12345').save(failOnError:true, flush:true))
			PollResponse.findByValue('Chuck-Norris').addToMessages(new TextMessage(text:'big charlie', date: new Date(), inbound: true, src: '12345').save(failOnError:true, flush:true))
			println "POLL MESSAGE COUNT: ${p.messages.size()}"
		then:
			p.responseStats == [
				[id:mjId, value:'Michael-Jackson', count:1, percent:50],
				[id:cnId, value:"Chuck-Norris", count:1, percent:50],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
		when:
			TextMessage.findByText('MJ').isDeleted = true
			TextMessage.findByText('MJ').save(flush:true)
		then:
			p.responseStats == [
				[id:mjId, value:'Michael-Jackson', count:0, percent:0],
				[id:cnId, value:'Chuck-Norris', count:1, percent:100],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
		when:
			def outbound = new TextMessage(inbound:false, text:'thanks for your response')
			outbound.addToDispatches(dst:"123", status:DispatchStatus.SENT, dateSent:new Date())
			p.addToMessages(outbound)
			p.save(failOnError:true, flush:true)
		then:
			p.responseStats == [
				[id:mjId, value:'Michael-Jackson', count:0, percent:0],
				[id:cnId, value:'Chuck-Norris', count:1, percent:100],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
	}

	def "creating a new poll also creates a poll response with value 'Unknown'"() {
		when:
			def p = new Poll(name: 'This is a poll')
			p.editResponses(choiceA: 'one', choiceB:'two')
			p.save(flush: true)
		then:
			p.responses.size() == 3
	}

    def "should sort messages based on date"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByName('question').getActivityMessages(false, null, null, [sort:'date', order:'desc'])
		then:
			results*.src == ["src2", "src3", "src1"]
			results.every {it.archived == false}
    }

	def "should fetch starred poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByName("question").getActivityMessages(true)
		then:
			results*.src == ["src3"]
			results.every {it.archived == false}
	}

	def "should check for offset and limit while fetching poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByName("question").getActivityMessages(false, null, null, [max:1, offset:0])
		then:
			results*.src == ["src2"]
	}

	def "should return count of poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByName("question").getActivityMessages().size()
		then:
			results == 3
	}
	
	def "adding responses to a poll with multiple responses does not affect categorized messages"() {
		when:
			def poll = new Poll(name:"title")
			poll.editResponses(choiceA: "one", choiceB: "two")
			poll.save(flush: true)
			def m1 = TextMessage.build(src: "src1", inbound: true, date: new Date() - 10)
			def m2 = TextMessage.build(src: "src2", inbound: true, date: new Date() - 10)
			def m3 = TextMessage.build(src: "src3", inbound: true, date: new Date() - 10)
			PollResponse.findByValue("one").addToMessages(m1)
			PollResponse.findByValue("one").addToMessages(m2)
			PollResponse.findByValue("two").addToMessages(m3)
			poll.save(flush:true)
			poll.refresh()
			def liveCount = { poll.responses.sort { ['A', 'B', 'C', 'D', Poll.KEY_UNKNOWN].indexOf(it.key) }*.liveMessageCount }
		then:
			liveCount() == [2, 1, 0]
		when:
			poll.editResponses(choiceC: "three", choiceD:"four")
			poll = Poll.get(poll.id)
		then:
			poll.responses*.value.containsAll(['one', 'two', 'three', 'four', 'Unknown'])
			liveCount() == [2, 1, 0, 0, 0]
		when:
			m1 = TextMessage.build(src: "src1", inbound: true, date: new Date() - 10)
			PollResponse.findByValue("one").addToMessages(m1)
			PollResponse.findByValue("three").addToMessages(TextMessage.build(src: "src4", inbound: true, date: new Date() - 10))
			poll.save(flush:true)
		then:
			liveCount() == [3, 1, 1, 0, 0]
		when:
			poll.editResponses(choiceA: "five")
			poll.save(flush:true)
			poll.refresh()
		then:
			!PollResponse.findByValue("one")
			println "poll responses ${poll.responses*.value}"
			poll.responses*.every {
				(it.key=='Unknown' && it.liveMessageCount == 3) ||
						(it.key == 'A' && it.liveMessageCount == 0)
			}
	}
	
	def "Archiving a poll archives messages associated with the poll"() {
		given:
			def poll = new Poll(name: 'Who is badder?')
			poll.editResponses(choiceA:'Michael-Jackson', choiceB:'Chuck-Norris')
			poll.save(failOnError:true, flush:true)

			def message1 = TextMessage.build(src:'Bob', text:'I like manchester')
			def message2 = TextMessage.build(src:'Alice', text:'go barcelona')
			poll.addToMessages(message1)
			poll.addToMessages(message2)
			poll.save(flush:true, failOnError:true)
		when:
			poll.archive()
			poll.save(flush:true, failOnError:true)
			poll.refresh()
		then:
			poll.liveMessageCount == 2
			poll.activityMessages.every { it.archived }
	}
	
	private def setUpPollAndResponses() {		
		def poll = new Poll(name: 'question')
		poll.addToResponses(PollResponse.createUnknown())
		poll.addToResponses(value:"response 1", key:'A')
		poll.addToResponses(value:"response 2", key:'B')
		poll.addToResponses(value:"response 3", key:'C')
		poll.save(flush: true, failOnError:true)
		return poll
	}

	private def setUpPollResponseAndItsMessages() {
		def poll = setUpPollAndResponses()
		def m1 = TextMessage.build(src: "src1", inbound: true, date: new Date() - 10)
		def m2 = TextMessage.build(src: "src2", inbound: true, date: new Date() - 2)
		def m3 = TextMessage.build(src: "src3", inbound: true, date: new Date() - 5, starred: true)
		PollResponse.findByValue("response 1").addToMessages(m1)
		PollResponse.findByValue("response 2").addToMessages(m2)
		PollResponse.findByValue("response 3").addToMessages(m3)
		poll.save(flush:true, failOnError:true)
	}

	def 'Adding a message will propogate it to the Unknown response'() {
		given:
			Poll p = setUpPollAndResponses()
			TextMessage m = TextMessage.build(date:new Date(), inbound:true, src:"a-unit-test!").save(flush:true, failOnError:true)
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

	// TODO move this test to MessageControllerISpec	
	def "Message should not remain in old PollResponse after moving it to inbox"(){
		given:
			def m = TextMessage.build(inbound:true)
			def responseA = new PollResponse(key:'A', value:'TessstA')
			def previousOwner = new Poll(name:'This is a poll', question:'What is your name?')
					.addToResponses(responseA)
					.addToResponses(key:'B' , value:'TessstB')
					.addToResponses(PollResponse.createUnknown())
			previousOwner.save(flush:true, failOnError:true)
			previousOwner.addToMessages(m).save(failOnError:true)
			responseA.addToMessages(m)

			assert responseA.refresh().messages.contains(m)
			
			// TODO move this test to MessageController
			def controller = new MessageController()
			controller.params.interactionId = m.id
			controller.params.ownerId = 'inbox'
			controller.params.messageSection = 'inbox'
		when:
			controller.move()
		then:
			!previousOwner.messages.contains(m)
			!responseA.messages.contains(m)
			!m.messageOwner
	}

	@Unroll
	def "Message should be sorted into the correct PollResponse for  Poll with top level and second level keywords"() {
		when:
			def p = TestData.createFootballPollWithKeywords()
		then:
			p.getPollResponse(new TextMessage(src:'Bob', text:"FOOTBALL something", inbound:true, date:new Date()).save(), Keyword.findByValue(keywordValue)).value == pollResponseValue
		where:
			keywordValue | pollResponseValue
			"FOOTBALL"   | "Unknown"
			"MANCHESTER" | "Manchester"	
			"BARCELONA"  | "Barcelona"
			"HARAMBEE"   | "Harambee Stars"
	}

	@Unroll
	def "Message should be sorted into the correct PollResponse for  Poll with only top level keywords"() {
		when:
			def p = TestData.createFootballPollWithKeywords()
		then:
			p.getPollResponse(new TextMessage(src:'Bob', text:"FOOTBALL something", inbound:true, date:new Date()).save(), p.keywords.find{ it.value == keywordValue }).value == pollResponseValue
		where:
			keywordValue | pollResponseValue
			"MANCHESTER" | "Manchester"
			"BARCELONA"  | "Barcelona"
			"HARAMBEE"   | "Harambee Stars"
	}

	def "saving a poll with a response value empty should fail"(){
		given:
			def p = new Poll(name: 'My Team poll')
			p.editResponses(choiceA: 'Manchester', choiceB:'Barcelona', aliasA: 'A,manu,yeah',aliasB: 'B,barca,bfc')
			p.save(failOnError:true)
			def controller = new PollController()
			controller.params.ownerId = p.id
			controller.params.choiceA = "My team"
			controller.params.choiceB = ""
		when:
			controller.save()
		then:
			p.refresh()
			p.responses*.value.containsAll(["Manchester", "Barcelona", "Unknown"])
	}
}
