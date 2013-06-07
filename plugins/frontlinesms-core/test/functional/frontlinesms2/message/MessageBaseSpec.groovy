package frontlinesms2.message

import frontlinesms2.*

class MessageBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestMessages() {
		remote {
			Fmessage.build(src:'Bob', text:'hi Bob')
			Fmessage.build(src:'Alice', text:'hi Alice')
			Fmessage.build(src:'+254778899', text:'test')
			null
		}
	}

	static createInboxTestMessages() {
		remote {
			Fmessage.build(src:'Bob', text:'hi Bob', date:new Date()-2)
			Fmessage.build(src:'Alice', text:'hi Alice', date:new Date()-1, starred:true)
			null
		}

		createMiaouwMixPoll()
	}

	static createPendingTestMessages() {
		remote {
			def a = Fmessage.buildWithoutSave(inbound:false)
			a.addToDispatches(Dispatch.buildWithoutSave(status: DispatchStatus.PENDING))
			a.save() // no-flush-deliberate

			def b = Fmessage.buildWithoutSave(inbound:false)
			b.addToDispatches(Dispatch.buildWithoutSave(status: DispatchStatus.PENDING))
			b.save() // no-flush-deliberate

			null
		}
	}

	static createSearchTestMessages() {
		remote {
			Fmessage.build(src:'Alex', text:'meeting at 11.00')
			Fmessage.build(src:'Bob', text:'hi Bob')
			Fmessage.build(src:'Michael', text:'Can we get meet in 5 minutes')
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

			Fmessage.build(src:'Bob',text:'hi Bob')
			Fmessage.build(src:'Alice', text:'hi Alice')
			Fmessage.build(src:'+254778899', text:'test')
			Fmessage.build(src:'Mary', text:'hi Mary')
			Fmessage.build(src:'+254445566', text:'test2')
			null
		}

		createMiaouwMixPoll()

		remote {
			def message1 = Fmessage.build(src:'Cheney', text:'i hate chicken')
			def message2 = Fmessage.build(src:'Bush', text:'i hate liver')
			def fools = Folder.build(name:'Fools')
			fools.addToMessages(message1)
			fools.addToMessages(message2)
			fools.save(failOnError:true, flush:true)
			null
		}
	}

	static createMiaouwMixPoll() {
		remote {
			def chickenMessage = Fmessage.build(src:'Barnabus', text:'i like chicken')
			def liverMessage = Fmessage.build(src:'Minime', text:'i like liver')

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

