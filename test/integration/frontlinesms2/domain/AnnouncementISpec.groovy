package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*
import grails.plugin.spock.*

class AnnouncementISpec extends grails.plugin.spock.IntegrationSpec  {
	def controller
	def setup() {
		controller = new AnnouncementController()
	}

	def "can save new announcement"() {
		setup:
			controller.params.name = "announcement"
			controller.params.messageText = "sending this"
		when:
			controller.save()
			def announcement = Announcement.findByName("announcement")
		then:
			announcement
			announcement.name == 'announcement'
			announcement.sentMessage == 'sending this'
	}
	
	def "A announcement can be archived"() {
		when:
			def a = new Announcement(name:'test', sentMessage: 'test message').save(failOnError:true)
		then:
			a.archived == false
		when:
			a.archive()
			a.save(failOnError:true, flush: true)
		then:
			a.archived == true
	}
	
	def "When an announcement is archived all of its messages are archived"() {
		setup:
			def a = new Announcement(name:'test', sentMessage: 'test message').save(failOnError:true)
			def m = new Fmessage()
		when:
			a.addToMessages(m)
			a.save()
		then:
			m.messageOwner == a
			a.archived == false
			m.archived == false
		when:
			a.archive()
			a.save(failOnError:true, flush: true)
		then:
			a.archived == true
			m.archived == true
	}
}

