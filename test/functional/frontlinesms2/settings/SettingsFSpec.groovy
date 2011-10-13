package frontlinesms2.settings

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import frontlinesms2.contact.PageContactShow

class SettingsFSpec extends SettingsBaseSpec {
	
	def 'settings menu item is available in system menu'() {
		when:
			to PageContactShow
			def btnGotoSettings = $('#system-menu #settings-nav a')
		then:
			btnGotoSettings.text() == 'Settings & Plugins'
	}
}
