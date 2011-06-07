package frontlinesms2

class PollSpec extends grails.plugin.spock.IntegrationSpec {
	def 'Deleted messages do not show up as responses'() {
		when:
			def response1 = new PollResponse(value:'Manchester').save()
			def response2 = new PollResponse(value:'Barcelona').save()
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound: true).save()
			def message2 = new Fmessage(src:'Alice', dst:'+2541234567', text:'go barcelona', inbound: true).save()
			response1.addToMessages(message1)
			response2.addToMessages(message2)
			def p = Poll.createPoll('This is a poll', [response1, response2]).save(failOnError:true, flush:true)
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
	
	def "creating a new poll also creates a poll response with value 'Unknown'"() {
		when:
			def responseList = [new PollResponse(value:'one'), new PollResponse(value:'other')]
			def p = Poll.createPoll('This is a poll', responseList)
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