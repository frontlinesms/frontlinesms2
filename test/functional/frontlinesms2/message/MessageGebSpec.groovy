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
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice')].each() {
					it.inbound = true
					it.save(failOnError:true)
				}
	}
	
	static createTestContacts() {	
		[new Contact(name: 'Alice', address: '+254778899'),
			new Contact(name: 'Bob', address: '+254987654')].each() { it.save(failOnError:true) }
	}
	
	static deleteTestMessages() {
		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
	
	static deleteTestContacts() {
		Contact.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}

