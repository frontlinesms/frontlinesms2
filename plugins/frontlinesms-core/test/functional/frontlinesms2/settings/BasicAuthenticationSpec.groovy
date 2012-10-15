package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class BasicAuthenticationSpec extends grails.plugin.geb.GebSpec {
	def 'can enable basic application authentication from the settings screen'() {
		when:
			to PageGeneralSettings
			basicAuthentication.enableAuthentication = true
			basicAuthentication.username = "test"
			basicAuthentication.password = "pass"
			basicAuthentication.save
		then:
			grailsApplication.config.frontlinesms.enabledAuthentication == true
			grailsApplication.config.frontlinesms.username == "test".bytes.encodeBase64().toString()
			grailsApplication.config.frontlinesms.password == "pass".bytes.encodeBase64().toString()
			
	}

	def 'can disable basic application authentication from the settings screen'() {
		setup: 
			grailsApplication.config.frontlinesms.enabledAuthentication = true
			grailsApplication.config.frontlinesms.username = "test".bytes.encodeBase64().toString()
			grailsApplication.config.frontlinesms.password = "pass".bytes.encodeBase64().toString()
		when:
			to PageGeneralSettings
			basicAuthentication.enableAuthentication = false
			basicAuthentication.save.click()
		then:
			grailsApplication.config.frontlinesms.enabledAuthentication == false
			grailsApplication.config.frontlinesms.username == "test".bytes.encodeBase64().toString()
			grailsApplication.config.frontlinesms.password == "pass".bytes.encodeBase64().toString()
			
	}

	def 'can edit authentication information from the settings screen'() {
		setup: 
			grailsApplication.config.frontlinesms.enabledAuthentication = true
			grailsApplication.config.frontlinesms.username = "test".bytes.encodeBase64().toString()
			grailsApplication.config.frontlinesms.password = "pass".bytes.encodeBase64().toString()
		when:
			to PageGeneralSettings
			basicAuthentication.username = "john"
			basicAuthentication.password = "doe"
			basicAuthentication.save.click()
		then:
			grailsApplication.config.frontlinesms.enabledAuthentication == false
			grailsApplication.config.frontlinesms.username == "john".bytes.encodeBase64().toString()
			grailsApplication.config.frontlinesms.password == "doe".bytes.encodeBase64().toString()
			
	}
}

