package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*
import grails.plugin.spock.*

class AnnouncementISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def setup() {
		controller = new AnnouncementController()
	}

	def "can save new announcement"() {
		setup:
			controller.params.name = "announcement"
			controller.params.addresses = "1234567890"
			controller.params.messageText = "sending this"
		when:
			controller.save()
			def announcement = Announcement.findByName("announcement")
		then:
			announcement.name == 'announcement'
			announcement.sentMessageText.contains('sending this')
			announcement
	}

	def "can edit an announcement"() {
		setup:
			def message = Fmessage.build()
			def announcement = new Announcement(name: 'Test', addresses: "12345")
			announcement.addToMessages(message)
			announcement.save(failOnError:true, flush:true)
			controller.params.ownerId = announcement.id
			controller.params.name = "renamed announcement"
		when:
			controller.save()
			def editedAnnouncement = Announcement.get(announcement.id)
		then:
			!Announcement.findByName('name')
			editedAnnouncement.name == "renamed announcement"
	}

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
	
	def "list of smart groups should be included in the group list"() {
		given:
			def s = new SmartGroup(name:'English numbers', mobile:'+44').save(flush:true)
		when:
			def model = controller.create()
		then:
			model.groupList["smartgroup-$s.id"]?.name == 'English numbers'
			model.groupList["smartgroup-$s.id"]?.addresses == []
	}
}

