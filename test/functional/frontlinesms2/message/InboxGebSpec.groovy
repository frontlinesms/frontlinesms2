package frontlinesms2.message

import frontlinesms2.*

class InboxGebSpec extends grails.plugin.geb.GebSpec {
    
    static createTestMessages() {
        [new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
            	new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob')].each() {
			it.save(failOnError:true)
		}
    }
    
    static deleteTestMessages() {
        Fmessage.findAll().each() {
            it.refresh()
            it.delete(failOnError:true, flush:true)
        }
    }    
}

