package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FolderSpec extends UnitSpec {
	def 'A folder may have none, one or many messages stored in it'() {
		given:
			mockDomain(MessageOwner)
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

