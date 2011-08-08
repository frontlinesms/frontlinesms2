package frontlinesms2.settings

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import frontlinesms2.contact.ContactListPage

class SettingsSpec extends grails.plugin.geb.GebSpec {
	
	def 'eyebrow list is present in the global navigation header'() {
		when:
			to ContactListPage
			def list = $("ul", id:"global-nav").children('li')
		then:
			assert list*.text() == ['Messages (0)', 'Archives', 'Contacts','Status', 'Search']
	}
	
	def 'settings menu item is available in system menu'() {
		when:
			to ContactListPage
			def btnGotoSettings = $('#system-menu #settings-nav')
		then:
			btnGotoSettings.text() == 'Settings & Plugins'
	}

	def 'PHONES & CONNECTIONS menu item is available settings menu'() {
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
			$("#settings-menu li:nth-child(1) a").hasClass('selected')
	}
}
