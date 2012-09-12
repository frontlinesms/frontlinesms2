package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(Trash)
class TrashSpec extends Specification {
	def 'findByObject should work'() {
		setup:
			def saved = new Trash(objectClass:HashMap, id:1).save()
		when:
			def found = Trash.findByObject([id:1])
		then:
			saved == found
	}
}

