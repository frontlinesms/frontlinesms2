package frontlinesms2.settings

import frontlinesms2.*

class GeneralSettingsSpec extends grails.plugin.geb.GebSpec {

	def "ensure database backup help is available"(){
		when:
			to PageGeneralSettings
		then:
			databaseBackup.displayed
	}
}