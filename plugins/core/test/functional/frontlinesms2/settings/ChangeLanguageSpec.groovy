package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class ChangeLanguageSpec extends SettingsBaseSpec {
	def 'language list should be available on the settings page and should be sorted alphabetically'() {
		when:
			to PageSettings
		then:
			languageList.children()*.text() == ['English', 'Kiswahili']
	}

	def 'Can change language of the application'() {
		given:
			to PageSettings
			assert title.contains('Settings')
		when:
			languageList.value('Kiswahili')
			btnApplyLanguage.click()
		then:
			title.contains('Mazingira')
		cleanup:
			languageList.value('English')
			btnApplyLanguage.click()
	}
}

