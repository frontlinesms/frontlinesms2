class SettingsPage extends geb.Page {
	static url = 'settings'
	static at = {
		title.endsWith('Settings')
	}
	static content = {
		phonesMenuItem { $("#settings-menu li").second() }
	}
}
