package frontlinesms2.message

import frontlinesms2.*

class MessageGebSpec extends grails.plugin.geb.GebSpec {
	
	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test')].each() {
					it.inbound = true
					it.save(failOnError:true)
				}
	}
	
	static createInboxTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 2),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice', dateReceived: new Date() - 1, starred: true)].each() {
					it.inbound = true
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', inbound:true)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver')
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		def poll = new Poll(title:'Miauow Mix')
		poll.addToResponses(chickenResponse)
		poll.addToResponses(liverResponse).save(failOnError:true, flush:true)
	}
	
	static createSearchTestMessages() {
		[new Fmessage(src:'Alex', dst:'+254987654', text:'meeting at 11.00'),
				new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Michael', dst:'+2541234567', text:'Can we get meet in 5 minutes')].each() {
					it.inbound = true
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', inbound:true)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver')
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
	}
	
	static createTestContacts() {	
		[new Contact(name: 'Alice', primaryMobile: '+254778899'),
			new Contact(name: 'Bob', primaryMobile: '+254987654')].each() { it.save(failOnError:true) }
	}
}

