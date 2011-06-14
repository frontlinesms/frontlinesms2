package frontlinesms2

class PollIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def 'Deleted messages do not show up as responses'() {
		when:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound: true).save()
			def message2 = new Fmessage(src:'Alice', dst:'+2541234567', text:'go barcelona', inbound: true).save()
			def p = Poll.createPoll('This is a poll', ['Manchester', 'Barcelona']).save(failOnError:true, flush:true)
			PollResponse.findByValue('Manchester').addToMessages(message1)
			PollResponse.findByValue('Barcelona').addToMessages(message2)
			p.save(flush:true, failOnError:true)
		then:
			p.messages.size() == 2
		when:
			message1.toDelete().save(flush:true, failOnError:true)
		then:
			p.messages.size() == 1
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
				[id:ukId, value:"Unknown", count:0, percent:0],
				[id:mjId, value:"Michael Jackson", count:0, percent:0],
				[id:cnId, value:"Chuck Norris", count:0, percent:0]
			]
		when:
			PollResponse.findByValue('Michael Jackson').addToMessages(new Fmessage(text:'MJ').save(failOnError:true, flush:true))
			PollResponse.findByValue('Chuck Norris').addToMessages(new Fmessage(text:'big charlie').save(failOnError:true, flush:true))
		then:
			p.responseStats == [
				[id:ukId, value:'Unknown', count:0, percent:0],
				[id:mjId, value:'Michael Jackson', count:1, percent:50],
				[id:cnId, value:"Chuck Norris", count:1, percent:50]
			]
		when:
			Fmessage.findByText('MJ').toDelete()
		then:
			p.responseStats == [
				[id:ukId, value:'Unknown', count:0, percent:0],
				[id:mjId, value:'Michael Jackson', count:0, percent:0],
				[id:cnId, value:'Chuck Norris', count:1, percent:100]
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
