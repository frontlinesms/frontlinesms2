package frontlinesms2.settings

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import frontlinesms2.contact.ContactListPage

class SettingsSpec extends grails.plugin.geb.GebSpec {
	
	def 'settings menu item is available in system menu'() {
		when:
			to ContactListPage
			def btnGotoSettings = $('#system-menu #settings-nav a')
		then:
			btnGotoSettings.text() == 'Settings & Plugins'
	}
}
