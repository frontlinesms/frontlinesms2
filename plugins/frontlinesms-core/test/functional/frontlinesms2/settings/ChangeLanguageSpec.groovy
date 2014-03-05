package frontlinesms2.settings

import frontlinesms2.*

import spock.lang.*

class ChangeLanguageSpec extends grails.plugin.geb.GebSpec {
	def 'language list should be available on the settings page and should be sorted alphabetically'() {
		when:
			to PageGeneralSettings
			def unsortedList = languageList.children()*.text()
		then:
			unsortedList.containsAll(['English', 'Deutsch', 'English', 'French', 'Indonesian', 'Japanese', 'Khmer', 'Kiswahili'])
			unsortedList.sort() == unsortedList
	}
}

