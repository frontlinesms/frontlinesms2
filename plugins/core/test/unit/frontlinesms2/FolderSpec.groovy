package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(Folder)
@Mock(Fmessage)
class FolderSpec extends Specification {
	def 'A folder may have none, one or many messages stored in it'() {
		when:
			def f = new Folder(name:'test')
		then:
			f.validate()
		when:
			f.addToMessages(new Fmessage())
		then:
			f.validate()
		when:
			f.addToMessages(new Fmessage())
		then:
			f.validate()
	}
}

