package frontlinesms2.services

import spock.lang.*
import frontlinesms2.SystemNotification
import frontlinesms2.SystemNotificationService
import frontlinesms2.I18nUtilService

@TestFor(SystemNotificationService)
@Mock([SystemNotification])

class SystemNotificationServiceSpec extends Specification {
	def setup() {
		def i18nUtilService = Mock(I18nUtilService)
		i18nUtilService.getMessage(_) >> { args -> args.code }
		service.i18nUtilService = i18nUtilService	
	}

	def "create should create a new SystemNotification"() {
		given:
			def code = "a.bit.of.i18n.code" 
		when:
			service.create(code:code, args:[], kwargs:[:])
		then:
			SystemNotification.count()
	}

	def 'setting topic of notification should mark all topics of that type as read'() {
		given:
			def startTopicNotification = new SystemNotification(text:"topic.start", topic:"CONNECTION.CREATE").save(failOnError:true)
			def code = "topic.notice"
			def topic = "CONNECTION.CREATE"
		when:
			def noticeTopicNotification = service.create(code:code, topic:topic)
		then:
			startTopicNotification.read
			!noticeTopicNotification.read
	}

	def 'test link substitution'() {
		expect:
			service.substituteLinks(input) == expected
		where:
			input | expected
			'Here is a link: [[stringA]]((stringB)).  I bet its interesting.' |
					'Here is a link: <a href="stringB">stringA</a>.  I bet its interesting.'
			'Failed to create connection on {1}: {2} [[[edit]]((#" onclick="mediumPopup.editConnection({0})))]' |
					'Failed to create connection on {1}: {2} [<a href="#" onclick="mediumPopup.editConnection({0})">edit</a>]'
			'Double trouble: [[one]]((http://1)), and then: [[two]]((http://2)).' |
					'Double trouble: <a href="http://1">one</a>, and then: <a href="http://2">two</a>.'
	}
}

