package frontlinesms2

import spock.lang.*

import grails.test.mixin.*

@TestFor(SecurityFilters)
@Mock([Group, GroupController, AppSettingsService])
class SecurityFiltersSpec extends Specification {
	def controller
	def appSettingsService

	def setup() {
		appSettingsService = ["auth.basic.enabled":true, "auth.basic.username":"bla", "auth.basic.password":"pass"]
		SecurityFilters.metaClass.appSettingsService = appSettingsService
		controller = new GroupController()
	}

	def "should prevents users from accessing the application pages when authentication is enabled"() {
		when:
			withFilters(action: 'list') {
				controller.list()
			}
		then:
			response.status == 401
	}

	def "should enable application access when the right credentials are specified"() {
		setup:
			appSettingsService."auth.basic.username" = "bla".bytes.encodeBase64().toString()
			appSettingsService."auth.basic.password" = "pass".bytes.encodeBase64().toString()
		when:
			def password = "bla:pass".bytes.encodeBase64().toString()
			request.addHeader('Authorization', password)
			withFilters(action: 'list') {
				controller.list()
			}
		then:
			response.status == 200
	}

	def "disabling password authentication should enable global application access"() {
		setup:
			appSettingsService."auth.basic.enabled" = false
			appSettingsService."auth.basic.username" = "bla"
			appSettingsService."auth.basic.password" = "pass".bytes.encodeBase64().toString()
		when:
			withFilters(action: 'list') {
				controller.list()
			}
		then:
			response.status == 200
	}
}
