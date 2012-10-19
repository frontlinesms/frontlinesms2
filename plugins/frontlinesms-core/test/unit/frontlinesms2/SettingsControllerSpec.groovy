package frontlinesms2

import spock.lang.*
import grails.test.mixin.*
import org.codehaus.groovy.grails.orm.hibernate.cfg.*

@TestFor(SettingsController)
@Mock([LogEntry, AppSettingsService])
class SettingsControllerSpec extends Specification {
	def TEST_DATE = new Date()

	def setup() {
		controller.i18nUtilService = Mock(I18nUtilService)
	}
	
	def "can view the list of all log entries"() {
		given:
			def entries = createLogEntries("12345":0, "message sent":10)
		when:
			params.timePeriod = 'forever'
			def model = controller.logs()
		then:
			model.logEntryList == entries
	}

	@Unroll
	def "can filter list of log entries by time"() {
		given:
			def entries = createLogEntries(entry1:0, entry2:2, entry3:6,
					entry4:13, entry5:27, entry66:100)
		when:
			params.timePeriod = timePeriod
			def model = controller.logs()
		then:
			model.logEntryList == entries[0..(entryCount-1)]
		where:
			timePeriod | entryCount
			'1'        | 1
			'3'        | 2
			'7'        | 3
			'14'       | 4
			'28'       | 5
			'forever'  | 6
	}

	def "can enable application authentication from settings if details validate"() {
		setup:
			// FIXME appSettingsService should be mocked
			def appSettingsService = new AppSettingsService()
			appSettingsService.load()
			SettingsController.metaClass.appSettingsService = appSettingsService
		when:
			params.enabledAuthentication = "on"
			params.username = "test"
			params.password = "pass"
			params.confirmPassword = "pass"
			controller.basicAuth()
		then:
			appSettingsService.get("enabledAuthentication") == "on"
			appSettingsService.get("username") == "test".bytes.encodeBase64().toString()
			appSettingsService.get("password") == "pass".bytes.encodeBase64().toString()
	}

	def "does not enable application authentication from settings if details validate"() {
		setup:
			// FIXME appSettingsService should be mocked
			def appSettingsService = new AppSettingsService()
			appSettingsService.load()
			appSettingsService.set("enabledAuthentication", false)
			appSettingsService.set("username", "")
			appSettingsService.set("password", "".bytes.encodeBase64().toString())
			SettingsController.metaClass.appSettingsService = appSettingsService
		when:
			params.enabledAuthentication = "on"
			params.username = "test"
			params.password = "pass"
			params.confirmPassword = "me"
			controller.basicAuth()
		then:
			!appSettingsService.get("enabledAuthentication")
			appSettingsService.get("username") == ""
			appSettingsService.get("password") == ""
	}

	private def createLogEntries(entries) {
		entries.collect { content, dateOffset ->
			new LogEntry(content:content, date:TEST_DATE-dateOffset).save(failOnError:true)
		}
	}
}

