package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class BasicAuthenticationSpec extends grails.plugin.geb.GebSpec {

	def url = "${this.config.properties.'grails.testing.functional.baseUrl'}${PageGeneralSettings.url}".replace("http://", "http://test:pass@")

	def 'can disable basic application authentication from the settings screen'() {
		when:
			setupAuthentication()
		then:
			at PageGeneralSettings
			basicAuthentication.enabledAuthentication
			basicAuthentication.username == "test"
			basicAuthentication.password == "pass"
		when:
			go url
			basicAuthentication.enabledAuthentication.click()
			basicAuthentication.confirmPassword = "pass"
			basicAuthentication.save.click()
		then:
			at PageGeneralSettings
			!basicAuthentication.enabledAuthentication.value()
			basicAuthentication.username == "test"
			basicAuthentication.password == "pass"
			
	}

	def 'can edit basic application authentication details'() {
		when:
			setupAuthentication()
		then:
			at PageGeneralSettings
			basicAuthentication.enabledAuthentication
			basicAuthentication.username == "test"
			basicAuthentication.password == "pass"
		when:
			go url
			basicAuthentication.username = "mark"
			basicAuthentication.password = "bla"
			basicAuthentication.confirmPassword = "bla"
			basicAuthentication.save.click()
		then:
			at PageGeneralSettings
			basicAuthentication.enabledAuthentication.value()
			basicAuthentication.username.disabled
			basicAuthentication.password.disabled
			
	}


	def "should show validation error when passwords don't match"() {
		when:
			go url.replace("test:pass", "mark:bla")
			at PageGeneralSettings
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

	def setupAuthentication() {
		go url
		at PageGeneralSettings
		basicAuthentication.enabledAuthentication.click()
		basicAuthentication.username = "test"
		basicAuthentication.password = "pass"
		basicAuthentication.confirmPassword = "pass"
		basicAuthentication.save.click()
	}
}

