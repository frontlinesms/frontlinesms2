package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class ChangeLanguageSpec extends SettingsBaseSpec {
	def 'language list should be available on the settings page'() {
		when:
			to PageSettings
		then:
			languageList.children()*.text() == ['Kiswahili', 'English']
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

