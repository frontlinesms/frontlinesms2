package frontlinesms2.poll

import frontlinesms2.*

class PollGebSpec extends grails.plugin.geb.GebSpec {
	static createTestPolls() {
		[new Poll(title:'Football Teams', responses:['manchester', 'barcelona']),
		new Poll(title:'Shampoo Brands', responses:['oriele', 'pantene'])].each() {
			it.save(failOnError:true, flush:true)
		}
	}

	static deleteTestPolls() {
		Poll.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}

