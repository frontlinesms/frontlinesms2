package frontlinesms2.controller

import frontlinesms2.*
import spock.lang.*

class AnnouncementControllerISpec extends grails.plugin.spock.IntegrationSpec {
	private static final String JSON_MIME_TYPE = 'application/json'

	def controller
	def i18nUtilService

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
			controller.flash.message == i18nUtilService.getMessage([code:"announcement.save.success", args:[announcement.name]])
			announcement.name == 'announcement'
			announcement.sentMessageText.contains('sending this')
			announcement
	}

	def "can edit an announcement"() {
		setup:
			def message = TextMessage.build()
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

	@Unroll
	def '#instanceCount announcement(s) can be fetched as a JSON list'() {
		given:
			// build a set of announcements
			instanceCount.times { Announcement.build() }
			// set requested format to JSON
			controller.request.format = JSON_MIME_TYPE
		when:
			controller.list()
		then:
			controller.response.contentAsString ==~ /\[(\{"id":\d+,"dateCreated":"\d+-\d\d-\d\dT\d\d:\d\d:\d\dZ","name":".*","sentMessageText":(".*"|null)\},?){$instanceCount}\]/
		where:
			instanceCount << [0, 1, 10]
	}

	def 'announcement list should not contain archived announcements'() {
		given:
			Announcement.build(archived:true)
			controller.request.format = JSON_MIME_TYPE
		when:
			controller.list()
		then:
			controller.response.contentAsString == '[]'
	}

	def 'announcement list should not contain deleted announcements'() {
		given:
			Announcement.build(deleted:true)
			controller.request.format = JSON_MIME_TYPE
		when:
			controller.list()
		then:
			controller.response.contentAsString == '[]'
	}
}
