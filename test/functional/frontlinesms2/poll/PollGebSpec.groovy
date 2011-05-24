package frontlinesms2.poll

import frontlinesms2.*

class PollGebSpec extends grails.plugin.geb.GebSpec {
	static createTestPolls() {

		[new Poll(title:'Football Teams', responses:[new PollResponse(value:'manchester'),
						new PollResponse(value:'barcelona')]),
				new Poll(title:'Shampoo Brands', responses:[new PollResponse(value:'pantene'),
						new PollResponse(value:'oriele')]),
				new Poll(title:'Rugby Brands', responses:[new PollResponse(value:'newzealand'),
						new PollResponse(value:'britain')])].each() {
			it.save(failOnError:true, flush:true)
		}
	}

	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'go manchester'),
				new Fmessage(src:'Joe', dst:'+254112233', text:'pantene is the best')].each() {
					it.inbound = true
					it.save(failOnError:true, flush:true)
				}

		[PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Bob')),
				PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Alice')),
				PollResponse.findByValue('pantene').addToMessages(Fmessage.findBySrc('Joe'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}

	static deleteTestPolls() {
		Poll.findAll().each() {
			it?.refresh()
			it?.delete(failOnError:true, flush:true)
		}
	}

	static deleteTestMessages() {
//		PollResponse.findByValue('manchester').removeFromMessages(Fmessage.findBySrc('Bob'))
//		PollResponse.findByValue('manchester').removeFromMessages(Fmessage.findBySrc('Alice'))
//		PollResponse.findByValue('pantene').removeFromMessages(Fmessage.findBySrc('Joe'))
//		PollResponse.findAll().each() {
//			it?.removeAllFromMessages()
//		}
		Fmessage.findAll().each() {
			it?.refresh()
			it?.delete(failOnError:true, flush:true)
		}
	}
}

