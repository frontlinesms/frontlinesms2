package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(Folder)
@Mock(Fmessage)
class FolderSpec extends Specification {
	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		Folder.metaClass.addToMessages = { m ->
			if(delegate.messages) delegate.messages << m
			else delegate.messages = [m]
			return delegate
		}
	}

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

