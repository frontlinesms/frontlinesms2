package frontlinesms2.controller

import frontlinesms2.*
import spock.lang.*

class AnnouncementControllerISpec extends grails.plugin.spock.IntegrationSpec {
	private static final String JSON_MIME_TYPE = 'application/json'

	def controller

	def setup() {
		controller = new AnnouncementController()
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
