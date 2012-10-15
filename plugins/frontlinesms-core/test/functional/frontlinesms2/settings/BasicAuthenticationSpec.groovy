package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class BasicAuthenticationSpec extends grails.plugin.geb.GebSpec {

	def 'can enable basic application authentication from the settings screen'() {
		when:
			setupAuthentication()
		then:
			at PageGeneralSettings
			basicAuthentication.enableAuthentication
			basicAuthentication.username == "test"
			basicAuthentication.password == "pass"
	}

	def "should show validation error when passwords don't match"() {
		when:
			to PageGeneralSettings
			basicAuthentication.username = "john"
			basicAuthentication.password = "doe"
			basicAuthentication.save.click()
		then:
			errors.displayed
		when:
			basicAuthentication.username = "john"
			basicAuthentication.password = "doe"
			basicAuthentication.confirmPassword = "daa"
			basicAuthentication.save.click()
		then:
			errors.displayed
	}

	def 'can disable basic application authentication from the settings screen'() {
		when:
			setupAuthentication()
		then:
			at PageGeneralSettings
			basicAuthentication.enableAuthentication
			basicAuthentication.username == "test"
			basicAuthentication.password == "pass"
		when:
			basicAuthentication.enableAuthentication = false
			basicAuthentication.save.click()
		then:
			at PageGeneralSettings
			!basicAuthentication.enableAuthentication
			basicAuthentication.username == "test"
			basicAuthentication.password == "pass"
			
	}

	def 'can edit authentication information from the settings screen'() {
		when: 
			setupAuthentication()
		then:
			at PageGeneralSettings
		when:
			basicAuthentication.username = "john"
			basicAuthentication.password = "doe"
			basicAuthentication.confirmPassword = "doe"
			basicAuthentication.save.click()
		then:
			at PageGeneralSettings
			basicAuthentication.enableAuthentication == "true"
			basicAuthentication.username == "john"
			basicAuthentication.password == "doe"
			
	}

	def setupAuthentication() {
		to PageGeneralSettings
		basicAuthentication.enableAuthentication = "enableAuthentication"
		basicAuthentication.username = "test"
		basicAuthentication.password = "pass"
		basicAuthentication.confirmPassword = "pass"
		basicAuthentication.save.click()
	}
}

