package frontlinesms2.poll

import frontlinesms2.*

class PollGebSpec extends grails.plugin.geb.GebSpec {
	static createTestPolls() {
		[Poll.createPoll('Football Teams', [new PollResponse(value:'manchester'),
						new PollResponse(value:'barcelona')]),
				Poll.createPoll('Shampoo Brands', [new PollResponse(value:'pantene'),
						new PollResponse(value:'oriele')]),
				Poll.createPoll('Rugby Brands', [new PollResponse(value:'newzealand'),
						new PollResponse(value:'britain')])].each() {
			it.save(failOnError:true, flush:true)
						}
	}

	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'go manchester'),
				new Fmessage(src:'Joe', dst:'+254112233', text:'pantene is the best'),
				new Fmessage(src:'Jill', dst:'+234234', text:'I fell down the hill')].each() {
					it.inbound = true
					it.save(failOnError:true, flush:true)
				}

		[PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Bob')),
				PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Alice')),
				PollResponse.findByValue('pantene').addToMessages(Fmessage.findBySrc('Joe'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}

	static createTestFolders() {
		[new Folder(value: 'Work'), 
			new Folder(value: 'Projects')].each() {
					it.save(failOnError:true, flush:true)
				}
	}
	
	static deleteTestPolls() {
		Poll.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

	static deleteTestMessages() {
		Fmessage.findAll().each() {
			it?.refresh()
			it?.delete(failOnError:true, flush:true)
		}
	}
	
	static deleteTestFolders() {
		Folder.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}

