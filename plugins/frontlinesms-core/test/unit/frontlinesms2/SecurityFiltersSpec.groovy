package frontlinesms2

import spock.lang.*

import grails.test.mixin.*

@TestFor(SecurityFilters)
@Mock([Group, GroupController])
class SecurityFiltersSpec extends Specification {
	def controller

	def setup() {
		grailsApplication.config.frontlinesms.enabledAuthentication = true
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
			grailsApplication.config.frontlinesms.username = "bla".bytes.encodeBase64().toString()
			grailsApplication.config.frontlinesms.password = "pass".bytes.encodeBase64().toString()
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
			grailsApplication.config.frontlinesms.enabledAuthentication = false
			grailsApplication.config.frontlinesms.username = 'bla'
			grailsApplication.config.frontlinesms.password = 'pass'
		when:
			withFilters(action: 'list') {
				controller.list()
			}
		then:
			response.status == 200
	}
}
