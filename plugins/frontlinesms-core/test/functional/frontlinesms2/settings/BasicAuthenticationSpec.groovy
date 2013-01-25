package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class BasicAuthenticationSpec extends grails.plugin.geb.GebSpec {

	def url = "${this.config.properties.'grails.testing.functional.baseUrl'}${PageGeneralSettings.url}".replace("http://", "http://test:pass@")

	def 'can add basic application authentication details'() {
		when:
			setupAuthentication()
		then:
			at PageGeneralSettings
			basicAuthentication.enabled
			basicAuthentication.username == 'test'
			basicAuthentication.password == '' // password should not be passed in HTML, even if set
		cleanup:
			basicAuthentication.enabled = false
			basicAuthentication.save.click()
	}

	def 'can disable basic application authentication from the settings screen'() {
		given:
			setupAuthentication()
		when:
			go url
			basicAuthentication.enabled = false
			basicAuthentication.save.click()
		then:
			at PageGeneralSettings
			waitFor { !basicAuthentication.enabled }
			basicAuthentication.username().disabled
			basicAuthentication.password().disabled
			
	}

	def "should show validation error when passwords don't match"() {
		when:
			go url
			at PageGeneralSettings
			basicAuthentication.enabled = true
			basicAuthentication.username = "john"
			basicAuthentication.password = "doe"
			basicAuthentication.save.click()
		then:
			waitFor { errors.displayed }
		when:
			basicAuthentication.username = "john"
			basicAuthentication.password = "doe"
			basicAuthentication.confirmPassword = "daa"
			basicAuthentication.save.click()
		then:
			waitFor { errors.displayed }
	}

	def setupAuthentication() {
		go url
		at PageGeneralSettings
		basicAuthentication.enabled = true
		basicAuthentication.username = "test"
		basicAuthentication.password = "pass"
		basicAuthentication.confirmPassword = "pass"
		basicAuthentication.save.click()
		at PageGeneralSettings
	}
}

