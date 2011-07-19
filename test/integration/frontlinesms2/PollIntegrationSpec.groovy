package frontlinesms2

import frontlinesms2.enums.MessageStatus

class PollIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def 'Deleted messages do not show up as responses'() {
		when:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save()
			def message2 = new Fmessage(src:'Alice', dst:'+2541234567', text:'go barcelona', status:MessageStatus.INBOUND).save()
			def p = Poll.createPoll('This is a poll', ['Manchester', 'Barcelona']).save(failOnError:true, flush:true)
			PollResponse.findByValue('Manchester').addToMessages(message1).save(failOnError: true)
			PollResponse.findByValue('Barcelona').addToMessages(message2).save(failOnError: true)
			p.save(flush:true, failOnError:true)
		then:
			p.getMessages(false).size() == 2
		when:
			message1.toDelete().save(flush:true, failOnError:true)
		then:
			p.getMessages(false).size() == 1
		cleanup:
			deleteTestData()
	}

	def 'Response stats are calculated correctly, even when messages are deleted'() {
		given:
			def p = Poll.createPoll('Who is badder?', ['Michael Jackson', 'Chuck Norris']).save(failOnError:true, flush:true)
		when:
			def ukId = PollResponse.findByValue('Unknown').id
			def mjId = PollResponse.findByValue('Michael Jackson').id
			def cnId = PollResponse.findByValue('Chuck Norris').id
		then:
			p.responseStats == [
				[id:mjId, value:"Michael Jackson", count:0, percent:0],
				[id:cnId, value:"Chuck Norris", count:0, percent:0],
				[id:ukId, value:"Unknown", count:0, percent:0]
			]
		when:
			PollResponse.findByValue('Michael Jackson').addToMessages(new Fmessage(text:'MJ').save(failOnError:true, flush:true))
			PollResponse.findByValue('Chuck Norris').addToMessages(new Fmessage(text:'big charlie').save(failOnError:true, flush:true))
		then:
			p.responseStats == [
				[id:mjId, value:'Michael Jackson', count:1, percent:50],
				[id:cnId, value:"Chuck Norris", count:1, percent:50],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
		when:
			Fmessage.findByText('MJ').toDelete()
		then:
			p.responseStats == [
				[id:mjId, value:'Michael Jackson', count:0, percent:0],
				[id:cnId, value:'Chuck Norris', count:1, percent:100],
				[id:ukId, value:'Unknown', count:0, percent:0]
			]
		cleanup:
			deleteTestData()
	}
	
	def "creating a new poll also creates a poll response with value 'Unknown'"() {
		when:
			def p = Poll.createPoll('This is a poll', ['one', 'two'])
		then:
			p.responses.size() == 3
	}

    def "should sort messages based on date received"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def result = Poll.findByTitle('question').getMessages(false)
		then:
			result*.src == ["src2", "src3", "src1"]
    }

	def "should fetch starred poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByTitle("question").getMessages(true)
		then:
			results*.src == ["src3"]
		cleanup:
			Folder.list()*.delete()
	}
	
	def "should check for offset and limit while fetching poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByTitle("question").getMessages(false,1, 0)
		then:
			results*.src == ["src2"]
		cleanup:
			Folder.list()*.delete()
	}

	def "should return count of poll messages"() {
		setup:
			setUpPollResponseAndItsMessages()
		when:
			def results = Poll.findByTitle("question").countMessages(false)
		then:
			results == 3
		cleanup:
			Folder.list()*.delete()
	}

	private def setUpPollResponseAndItsMessages() {
		def poll = new Poll(title: 'question')
		poll.addToResponses(new PollResponse(value: "response 1"))
		poll.addToResponses(new PollResponse(value: "response 2"))
		poll.addToResponses(new PollResponse(value: "response 3"))
		poll.save(flush: true, failOnError:true)

		PollResponse.findByValue("response 1").addToMessages(new Fmessage(src: "src1", dateReceived: new Date() - 10)).save(flush: true, failOnError:true)
		PollResponse.findByValue("response 2").addToMessages(new Fmessage(src: "src2", dateReceived: new Date() - 2)).save(flush: true, failOnError:true)
		PollResponse.findByValue("response 3").addToMessages(new Fmessage(src: "src3", dateReceived: new Date() - 5, starred: true)).save(flush: true, failOnError:true)
	}

	
	static deleteTestData() {
		Poll.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}

		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}

