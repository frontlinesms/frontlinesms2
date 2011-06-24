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
	
	def "Adding a message to a Folder will cascade to the message's activity value"() {
		// FIXME this almost certainly needs to be an integration test due to reliance on cascades (cascades are probably enforced by Hibernate rather than GORM)
		given:
			mockDomain(Fmessage)
			mockDomain(MessageOwner)
			def f = new Folder(name:'test').save(failOnError:true)
			def m = new Fmessage()
		when:
			f.addToMessages(m)
			f.save()
		then:
			m.messageOwner == f
	}
}

