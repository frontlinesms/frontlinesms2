package frontlinesms2.poll

import frontlinesms2.*

class PollBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestPolls() {
		remote {
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
			null
		}
	}

	static createTestMessages() {
		remote {
			TextMessage.build(src:'Bob', text:'I like manchester', date:new Date()-4, starred:true)
			TextMessage.build(src:'Alice', text:'go manchester', date:new Date()-3)
			TextMessage.build(src:'Joe', text:'pantene is the best',  date:new Date()-2)
			TextMessage.build(src:'Jill', text:'I fell down the hill',  date:new Date()-1)

			def poll = Poll.findByName('Football Teams')
			[PollResponse.findByValue('manchester').addToMessages(TextMessage.findBySrc('Bob')),
					PollResponse.findByValue('manchester').addToMessages(TextMessage.findBySrc('Alice')),
					PollResponse.findByValue('pantene').addToMessages(TextMessage.findBySrc('Joe'))]
			poll.save(failOnError:true, flush:true)
			null
		}
	}

	static createMoreTestMessages() {
		remote {
			TextMessage.build(src:'Jill', text:'barcelona sucks!', starred:true)
			TextMessage.build(src:'Tony', text:'Gormahia!')

			PollResponse.findByValue('manchester').addToMessages(TextMessage.findByText('barcelona sucks!'))
			PollResponse.findByValue('barcelona').addToMessages(TextMessage.findBySrc('Tony'))
			Poll.findByName('Football Teams').save(failOnError:true, flush:true)
			null
		}
	}

	static createTestFolders() {
		remote {
			['Work', 'Projects'].each {
				Folder.build(name: it)
			}
			null
		}
	}
}

