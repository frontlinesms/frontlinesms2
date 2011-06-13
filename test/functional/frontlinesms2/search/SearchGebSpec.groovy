package frontlinesms2.search

import frontlinesms2.*

class SearchGebSpec extends grails.plugin.geb.GebSpec{
	
	def createTestGroups() {
		new Group(name: 'Listeners').save(flush: true)
		new Group(name: 'Friends').save(flush: true)
	}
	
	static createTestMessages() {
		[new Fmessage(src:'Alex', dst:'+254987654', text:'meeting at 11.00'),
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Michael', dst:'+2541234567', text:'Can we get meet in 5 minutes')].each() {
					it.inbound = true
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', inbound:true)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver', inbound:false)
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
	}
	
	static deleteTestMessages() {
		Poll.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}

		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
	
	static deleteTestGroups() {
		Group.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}

