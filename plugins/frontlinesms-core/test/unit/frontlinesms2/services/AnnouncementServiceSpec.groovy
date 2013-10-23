package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*
import grails.buildtestdata.mixin.Build

@Mock(MessageSendService)
@Build(Announcement)
@TestFor(AnnouncementService)
class AnnouncementServiceSpec extends Specification {
	def messageSendService
	def announcement

	def setup() {
		announcement = Announcement.build()
		announcement.metaClass.addToMessages = { m->  return }

		messageSendService = Mock(MessageSendService)
		messageSendService.createOutgoingMessage(_) >> true
		messageSendService.send(_) >> true

		service.messageSendService = messageSendService
	}

	def 'announcement message should not be sent if announcement fails validation'() {
		setup:
			announcement.metaClass.save = { Map m -> false }
		when:
			service.saveInstance(announcement, [name:'this is the name', messageText:'send me'])
		then:
			1 * messageSendService.createOutgoingMessage(_)
			0 * messageSendService.send(_)
	}

	def 'announcement message should be sent only if announcement passes validation'() {
		setup:
			announcement.metaClass.save = { Map m -> true }
		when:
			service.saveInstance(announcement, [name:'this is the name', messageText:'send me'])
		then:
			1 * messageSendService.createOutgoingMessage(_)
			1 * messageSendService.send(_)
	}
}