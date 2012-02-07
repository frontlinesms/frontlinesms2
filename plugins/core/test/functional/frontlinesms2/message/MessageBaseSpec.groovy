package frontlinesms2.message

import frontlinesms2.*

class MessageBaseSpec extends grails.plugin.geb.GebSpec {
	
	static createTestMessages() {
		[new Fmessage(src:'Bob', text:'hi Bob', date: new Date()),
				new Fmessage(src:'Alice', text:'hi Alice', date: new Date()),
				new Fmessage(src:'+254778899', text:'test', date: new Date())].each() {
					it.inbound = true
					it.save(flush:true, failOnError:true)
				}
	}
	
	static createInboxTestMessages() {
		[new Fmessage(src:'Bob', text:'hi Bob', date: new Date() - 2),
				new Fmessage(src:'Alice', text:'hi Alice', date: new Date() - 1, starred: true)].each() {
					it.inbound = true
					it.save(flush:true, failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', text:'i like chicken', inbound:true, date: new Date())
		def liverMessage = new Fmessage(src:'Minime', text:'i like liver', date: new Date(), inbound: true)
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		def poll = new Poll(name:'Miauow Mix')
		poll.addToResponses(chickenResponse)
		poll.addToResponses(liverResponse)
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		poll.save(flush:true, failOnError:true)
	}
	
	static createSearchTestMessages() {
		[new Fmessage(src:'Alex', text:'meeting at 11.00'),
				new Fmessage(src:'Bob', text:'hi Bob'),
				new Fmessage(src:'Michael', text:'Can we get meet in 5 minutes')].each() {
					it.inbound = true
					it.date = new Date()
					it.save(failOnError:true, flush:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', text:'i like chicken', inbound:true, date: new Date())
		def liverMessage = new Fmessage(src:'Minime', text:'i like liver', date: new Date(), inbound:true)
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
	}
	
	static createTestContacts() {	
		[new Contact(name: 'Alice', primaryMobile: '+254778899'),
			new Contact(name: 'Bob', primaryMobile: '+254987654')].each() { it.save(flush:true, failOnError:true) }
	}
	
	static createTestData() {
		[new Contact(name: 'Bob', primaryMobile: '+254987654')].each() {it.save(flush:true, failOnError:true)}
		
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test')].each() {
					it.inbound = true
					it.save(flush:true, failOnError:true)
				}
		[new Fmessage(src:'Mary', dst:'+254112233', text:'hi Mary'),
				new Fmessage(src:'+254445566', dst:'+254112233', text:'test')].each() {
					it.save(flush:true, failOnError:true)
				}
		
		def chickenMessage = new Fmessage(src:'Barnabus', text:'i like chicken', inbound:true, date: new Date())
		def liverMessage = new Fmessage(src:'Minime', text:'i like liver', inbound: true, date: new Date())
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		def poll = new Poll(title:'Miauow Mix')
		poll.addToResponses(chickenResponse)
		poll.addToResponses(liverResponse).save(flush:true, failOnError:true)

		def message1 = new Fmessage(src:'Cheney', text:'i hate chicken', inbound:true, date: new Date())
		def message2 = new Fmessage(src:'Bush', text:'i hate liver', inbound:true, date: new Date())
		def fools = new Folder(name:'Fools').save(flush:true, failOnError:true)
		fools.addToMessages(message1)
		fools.addToMessages(message2)
		fools.save(failOnError:true, flush:true)
	}
	
}

