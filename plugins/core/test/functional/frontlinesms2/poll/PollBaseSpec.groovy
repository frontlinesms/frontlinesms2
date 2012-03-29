package frontlinesms2.poll

import frontlinesms2.*

class PollBaseSpec extends grails.plugin.geb.GebSpec {
	
	static createTestPolls() {
		def poll1 = new Poll(name: 'Football Teams')
		poll1.addToResponses(new PollResponse(key: 'A', value: 'manchester'))
		poll1.addToResponses(new PollResponse(key: 'B', value: 'barcelona'))
		poll1.addToResponses(PollResponse.createUnknown())
		poll1.save(failOnError:true, flush:true)

		def poll2 = new Poll(name: 'Shampoo Brands')
		poll2.addToResponses(new PollResponse(key: 'A', value: 'pantene'))
		poll2.addToResponses(new PollResponse(key: 'B', value: 'oriele'))
		poll2.addToResponses(PollResponse.createUnknown())
		poll2.save(failOnError:true, flush:true)
		
		def poll3 = new Poll(name: 'Rugby Brands')
		poll3.addToResponses(new PollResponse(key: 'A', value: 'newzealand'))
		poll3.addToResponses(new PollResponse(key: 'B', value: 'britain'))
		poll3.addToResponses(PollResponse.createUnknown())
		poll3.save(failOnError:true, flush:true)
	}

	static createTestMessages() {
		[new Fmessage(src:'Bob', text:'I like manchester', date: new Date() - 4, starred: true),
			new Fmessage(src:'Alice', text:'go manchester', date: new Date() - 3),
				new Fmessage(src:'Joe', text:'pantene is the best',  date: new Date() - 2),
				new Fmessage(src:'Jill', text:'I fell down the hill',  date: new Date() - 1)].each() {
					it.inbound = true
					it.save(failOnError:true, flush:true)
				}
		def poll = Poll.findByName('Football Teams')	
		[PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Bob')),
				PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Alice')),
				PollResponse.findByValue('pantene').addToMessages(Fmessage.findBySrc('Joe'))]
		poll.save(failOnError:true, flush:true)
	}
	
	static createMoreTestMessages() {
		[new Fmessage(src:'Jill', text:'barcelona sucks!', date: new Date(), starred: true),
			new Fmessage(src:'Tony', text:'Gormahia!', date: new Date())].each() {
					it.inbound = true
					it.save(failOnError:true, flush:true)
				}

		PollResponse.findByValue('manchester').addToMessages(Fmessage.findByText('barcelona sucks!'))
		PollResponse.findByValue('barcelona').addToMessages(Fmessage.findBySrc('Tony'))
		Poll.findByName('Football Teams').save(failOnError:true, flush:true)
	}

	static createTestFolders() {
		['Work', 'Projects'].each {
			new Folder(name: it).save(failOnError:true, flush:true)
		}
	}
}

