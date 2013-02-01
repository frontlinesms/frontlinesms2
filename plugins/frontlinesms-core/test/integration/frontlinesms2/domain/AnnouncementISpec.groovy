package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*
import grails.plugin.spock.*

class AnnouncementISpec extends grails.plugin.spock.IntegrationSpec {

	def "A announcement can be archived"() {
		when:
			def m = Fmessage.build()
			def a = new Announcement(name:'test')
			a.addToMessages(m)
			a.save(failOnError:true)
		then:
			!a.archived
		when:
			a.archive()
			println "a.archived:$a.archived"
			a.messages.each { println("...it.archived:$it.archived") }
			a.save(failOnError:true, flush: true)
		then:
			a.archived
	}
	
	def "When an announcement is archived all of its messages are archived"() {
		setup:
			def a = new Announcement(name:'test1x')
			a.addToMessages(Fmessage.build())
			a.save(failOnError:true)
			def m = Fmessage.build()
		when:
			a.addToMessages(m)
			a.save()
		then:
			m.messageOwner == a
			!a.archived
			!m.archived
		when:
			a.archive()
			a.save()
		then:
			a.archived
			m.archived
	}

}

