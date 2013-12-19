package frontlinesms2.message

import frontlinesms2.*

class MessageBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestMessages() {
		remote {
			TextMessage.build(src:'Bob', text:'hi Bob')
			TextMessage.build(src:'Alice', text:'hi Alice')
			TextMessage.build(src:'+254778899', text:'test')
			null
		}
	}

	static createInboxTestMessages() {
		remote {
			TextMessage.build(src:'Bob', text:'hi Bob', date:new Date()-2)
			TextMessage.build(src:'Alice', text:'hi Alice', date:new Date()-1, starred:true)
			null
		}

		createMiaouwMixPoll()
	}

	static createPendingTestMessages() {
		remote {
			def a = TextMessage.buildWithoutSave(inbound:false)
			a.addToDispatches(Dispatch.buildWithoutSave(status: DispatchStatus.PENDING))
			a.save() // no-flush-deliberate

			def b = TextMessage.buildWithoutSave(inbound:false)
			b.addToDispatches(Dispatch.buildWithoutSave(status: DispatchStatus.PENDING))
			b.save() // no-flush-deliberate

			null
		}
	}

	static createSearchTestMessages() {
		remote {
			TextMessage.build(src:'Alex', text:'meeting at 11.00')
			TextMessage.build(src:'Bob', text:'hi Bob')
			TextMessage.build(src:'Michael', text:'Can we get meet in 5 minutes')
			null
		}

		createMiaouwMixPoll()
	}

	static createTestContacts() {
		remote {
			Contact.build(name:'Alice', mobile:'+254778899')
			Contact.build(name:'Bob', mobile:'+254987654')
			null
		}
	}

	static createTestData() {
		remote {
			Contact.build(name:'Bob', mobile:'+254987654')

			TextMessage.build(src:'Bob',text:'hi Bob')
			TextMessage.build(src:'Alice', text:'hi Alice')
			TextMessage.build(src:'+254778899', text:'test')
			TextMessage.build(src:'Mary', text:'hi Mary')
			TextMessage.build(src:'+254445566', text:'test2')
			null
		}

		createMiaouwMixPoll()

		remote {
			def message1 = TextMessage.build(src:'Cheney', text:'i hate chicken')
			def message2 = TextMessage.build(src:'Bush', text:'i hate liver')
			def fools = Folder.build(name:'Fools')
			fools.addToMessages(message1)
			fools.addToMessages(message2)
			fools.save(failOnError:true, flush:true)
			null
		}
	}

	static createMiaouwMixPoll() {
		remote {
			def chickenMessage = TextMessage.build(src:'Barnabus', text:'i like chicken')
			def liverMessage = TextMessage.build(src:'Minime', text:'i like liver')

			def poll = new Poll(name:'Miauow Mix')
			def chickenResponse = new PollResponse(key:'A', value:'chicken')
			def liverResponse = new PollResponse(key:'B', value:'liver')
			poll.addToResponses(chickenResponse)
			poll.addToResponses(liverResponse)
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(flush:true, failOnError:true)

			chickenResponse.addToMessages(chickenMessage)
			liverResponse.addToMessages(liverMessage)
			poll.save(flush:true, failOnError:true)
			null
		}
	}
}

