package frontlinesms2.settings

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import frontlinesms2.contact.ContactListPage

class SettingsSpec extends grails.plugin.geb.GebSpec {
	def 'settings menu item is available in eyebrow'() {
		when:
			to ContactListPage
			def btnGotoSettings = $('#eyebrow #goto-settings')
		then:
			btnGotoSettings.text() == 'Settings & Plugins'
		when:
			btnGotoSettings.click()
		then:
			at SettingsPage
	}

	def '"phones & connections" menu item is available settings menu'() {
		when:
			to SettingsPage
		then:
			phonesMenuItem.text() == "Phones & connections"
			phonesMenuItem.children('a').getAttribute('href') == "/frontlinesms2/connection/index"
	}
	
	def 'The first item in the settings page is selected'() {
		when:
			to SettingsPage
		then:
			$("li:nth-child(1) .selected")
	}
}
