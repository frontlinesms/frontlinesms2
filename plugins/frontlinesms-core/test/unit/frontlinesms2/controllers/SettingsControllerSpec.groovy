package frontlinesms2.controllers

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*
import org.codehaus.groovy.grails.orm.hibernate.cfg.*

@TestFor(SettingsController)
@Mock([LogEntry, AppSettingsService, Fconnection, SmslibFconnection])
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
			params.enabled = 'true'
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

	def "should not enable application authentication from settings if details don't validate"() {
		given:
			mockAppSettings('auth.basic.enabled': false,
					username:'', password:'')
		when:
			params.enabled = "true"
			params.username = "test"
			params.password = "pass"
			params.confirmPassword = "me"
			controller.basicAuth()
		then:
			0 * appSettingsService.set(_, _)
	}

	def 'can set the routing preferences'(){
		given:
			params.routingUseOrder = "uselastreceiver"
		when:
			controller.changeRoutingPreferences()
		then:
			1 * appSettingsService.set('routing.use','uselastreceiver')
			0 * appSettingsService.set(_, _)
	}

	def "can set routing rules available connections"() {
		given:
			params.routingUseOrder = "uselastreceiver,fconnection-1,fconnection-3,fconnection-5"
		when:
			controller.changeRoutingPreferences()
		then:
			1 * appSettingsService.set('routing.use','uselastreceiver,fconnection-1,fconnection-3,fconnection-5')
	}

	def "can retrieve routing rules defined for connections with send enabled"() {
		given:
			def conn1 = new SmslibFconnection(name:"Huawei Modem", port:'/dev/cu.HUAWEIMobile-Modem', baud:9600, pin:'1234').save(failOnError:true)
			def conn2 = new SmslibFconnection(name:"COM4", port:'COM4', baud:9600).save(failOnError:true)
			new SmslibFconnection(name:"COM5", port:'COM5', baud:9600, sendEnabled:false).save(failOnError:true)
			controller.appSettingsService = ['routing.use':"uselastreceiver,fconnection-${conn2.id},fconnection-${conn1.id}"]
		when:
			def model = controller.general()
		then:
			model.fconnectionRoutingMap*.key*.toString() == ["uselastreceiver", conn2, conn1]*.toString()
			model.fconnectionRoutingMap*.value == [true,true,true]
	}

	def "should not display routing rules for devices that have been deleted from the system"() {
		given:
			def conn1 = new SmslibFconnection(name:"Modem", port:'/dev/cu.HUAWEIMobile-Modem', baud:9600, pin:'1234').save(failOnError:true)
			def conn2 = new SmslibFconnection(name:"COM5", port:'COM4', baud:9600).save(failOnError:true)
			controller.appSettingsService = ['routing.use':"uselastreceiver,fconnection-${conn2.id},fconnection-3,fconnection-${conn1.id}"]
		when:
			def model = controller.general()
		then:
			model.fconnectionRoutingMap*.key*.toString() == ['uselastreceiver', conn2, conn1]*.toString()
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

