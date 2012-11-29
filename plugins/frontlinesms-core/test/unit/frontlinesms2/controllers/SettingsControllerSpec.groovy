package frontlinesms2.controllers

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*
import org.codehaus.groovy.grails.orm.hibernate.cfg.*

@TestFor(SettingsController)
@Mock([LogEntry, AppSettingsService])
class SettingsControllerSpec extends Specification {
	def TEST_DATE = new Date()

	def appSettingsService

	def setup() {
		controller.i18nUtilService = Mock(I18nUtilService)
		appSettingsService = Mock(AppSettingsService)
		controller.appSettingsService = appSettingsService
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
		given:
			params.enabledAuthentication = 'true'
			params.username = "test"
			params.password = "pass"
			params.confirmPassword = "pass"
		when:
			controller.basicAuth()
		then:
			1 * appSettingsService.set('auth.basic.enabled', 'true')
			1 * appSettingsService.set('auth.basic.username', 'test'.bytes.encodeBase64().toString())
			1 * appSettingsService.set('auth.basic.password', 'pass'.bytes.encodeBase64().toString())
			0 * appSettingsService.set(_, _)
	}

	def 'can enabled application update checking from settings'(){
		given:
			params.checkUpdates = true
		when:
			controller.saveCheckUpdates()
		then:
			1 * appSettingsService.set('version.check.updates', (params.checkUpdates?true:false))
	}

	def "should not enable application authentication from settings if details don't validate"() {
		given:
			mockAppSettings(enabledAuthentication: false,
					username:'', password:'')
		when:
			params.enabledAuthentication = "true"
			params.username = "test"
			params.password = "pass"
			params.confirmPassword = "me"
			controller.basicAuth()
		then:
			0 * appSettingsService.set(_, _)
	}

	private def mockAppSettings(Map s) {
		s.each { k, v ->
			appSettingsService.get(k, _) >> { v instanceof String? v.bytes.encodeBase64().toString(): v }
		}
	}

	private def createLogEntries(entries) {
		entries.collect { content, dateOffset ->
			new LogEntry(content:content, date:TEST_DATE-dateOffset).save(failOnError:true)
		}
	}
}

