package frontlinesms2.poll

import frontlinesms2.*

class PollBaseSpec extends grails.plugin.geb.GebSpec {
	
	static createTestPolls() {
		def poll1 = new Poll(name:'Football Teams')
		poll1.addToResponses(key:'A', value:'manchester')
		poll1.addToResponses(key:'B', value:'barcelona')
		poll1.addToResponses(PollResponse.createUnknown())
		poll1.save(failOnError:true, flush:true)

		def poll2 = new Poll(name:'Shampoo Brands')
		poll2.addToResponses(key:'A', value:'pantene')
		poll2.addToResponses(key:'B', value:'oriele')
		poll2.addToResponses(PollResponse.createUnknown())
		poll2.save(failOnError:true, flush:true)
		
		def poll3 = new Poll(name:'Rugby Brands')
		poll3.addToResponses(key:'A', value:'newzealand')
		poll3.addToResponses(key:'B', value:'britain')
		poll3.addToResponses(PollResponse.createUnknown())
		poll3.save(failOnError:true, flush:true)
	}

	static createTestMessages() {
		Fmessage.build(src:'Bob', text:'I like manchester', date:new Date()-4, starred:true)
		Fmessage.build(src:'Alice', text:'go manchester', date:new Date()-3)
		Fmessage.build(src:'Joe', text:'pantene is the best',  date:new Date()-2)
		Fmessage.build(src:'Jill', text:'I fell down the hill',  date:new Date()-1)

		def poll = Poll.findByName('Football Teams')	
		[PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Bob')),
				PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Alice')),
				PollResponse.findByValue('pantene').addToMessages(Fmessage.findBySrc('Joe'))]
		poll.save(failOnError:true, flush:true)
	}
	
	static createMoreTestMessages() {
		Fmessage.build(src:'Jill', text:'barcelona sucks!', starred:true)
		Fmessage.build(src:'Tony', text:'Gormahia!')

		PollResponse.findByValue('manchester').addToMessages(Fmessage.findByText('barcelona sucks!'))
		PollResponse.findByValue('barcelona').addToMessages(Fmessage.findBySrc('Tony'))
		Poll.findByName('Football Teams').save(failOnError:true, flush:true)
	}

	static createTestFolders() {
		['Work', 'Projects'].each {
			Folder.build(name: it)
		}
	}
}

