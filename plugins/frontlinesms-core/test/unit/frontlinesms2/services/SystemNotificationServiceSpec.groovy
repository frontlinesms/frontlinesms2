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
			service.create([code:code, args:[], kwargs:[:]])
		then:
			SystemNotification.count()
	}
}
