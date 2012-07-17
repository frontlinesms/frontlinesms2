package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class ChangeLanguageSpec extends grails.plugin.geb.GebSpec {
	def 'language list should be available on the settings page and should be sorted alphabetically'() {
		when:
			to PageGeneralSettings
		then:
			languageList.children()*.text()== ['English', 'English', 'Kiswahili']
	}

	def 'Can change language of the application'() {
		given:
			to PageGeneralSettings
			assert title.contains('Settings')
		when:
			languageList.value('Kiswahili')
		then:
			title.contains('Mazingira')
		cleanup:
			languageList.value('English')
	}
}

