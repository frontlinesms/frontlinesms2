package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class AutoreplyControllerSpec extends ControllerSpec {

	def "can save an Autoreply"() {
		given:
			mockDomain(Autoreply)
		when:
			mockParams.name = "Color"
			mockParams.keyword = "color"
			mockParams.messageText = "ahhhhhhhhh"
			controller.save()
		then:
			!poll.archived
			controller.redirectArgs == [controller:'archive', action:'activityList']
	}
	
	def "can edit an Autoreply"() {
		given:
			mockDomain(Autoreply)
		when:
			mockParams.id = poll.id
			controller.unarchive()
		then:
			!poll.archived
			controller.redirectArgs == [controller:'archive', action:'activityList']
	}
	
	@Unroll
	def 'all activities should be archivable'() {
		expect:
			def activity = activityClass.build().archive()
			activity.archived
		where:
			activityClass
			Poll
			AutoReply
			Announcement
	}
}

