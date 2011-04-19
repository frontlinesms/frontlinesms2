package frontlinesms2

class SettingsSpec extends grails.plugin.geb.GebSpec {
	def 'settings menu item is available in eyebrow'() {
		when:
			to ContactsListPage
			def btnGotoSettings = $('#eyebrow #btnGotoSettings a')
		then:
			btnGotoSettings.text() == 'Settings & Plugins'
		when:
			btnGotoSettings.click()
		then:
			at SettingsPage
	}
}
