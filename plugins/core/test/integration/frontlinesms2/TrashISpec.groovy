package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import grails.test.mixin.*

class TrashISpec extends IntegrationSpec {
	def 'findByObject should work'() {
		setup:
			def saved = new Trash(objectClass:HashMap, id:1).save()
		when:
			def found = Trash.findByObject([id:1])
		then:
			saved == found
	}

	def 'findByObject should work on polls'() {
		setup:
			Poll p = new Poll(name:'test')
					.addToResponses(key:'A', value:'eh')
					.addToResponses(key:'B', value:'bee')
					.addToResponses(key:'C', value:'sea')
					.save(failOnError:true)
			def saved = new Trash(objectId:p.id, objectClass:p.getClass()).save()
		when:
			def found = Trash.findByObject(p)
		then:
			saved == found
	}
}

