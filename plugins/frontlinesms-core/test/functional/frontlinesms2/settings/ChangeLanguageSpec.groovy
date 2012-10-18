package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class ChangeLanguageSpec extends grails.plugin.geb.GebSpec {
	def 'language list should be available on the settings page and should be sorted alphabetically'() {
		when:
			to PageGeneralSettings
		then:
			languageList.children()*.text()== ["English", "Arabic", "Deutsch", "English", "Español", "French", "Indonesian", "Kiswahili", "Português", "Русский"]
	}

	def 'Can change language of the application'() {
		given:
			to PageGeneralSettings
			waitFor { title.contains('Settings') }
		when:
			languageList.value('Kiswahili')
		then:
			waitFor { title.contains('Mazingira') }
		cleanup:
			languageList.jquery.val('English')
			languageList.jquery.trigger('change')
			waitFor { title.contains('Settings') }
	}
}

