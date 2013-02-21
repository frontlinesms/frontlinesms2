package frontlinesms2.settings

import frontlinesms2.*

class GeneralSettingsSpec extends grails.plugin.geb.GebSpec {

	def "ensure database backup help is available"(){
		when:
			to PageGeneralSettings
		then:
			databaseBackup.displayed
			databaseBackup.title == 'configuration location'
			databaseBackup.instruction.contains('database')
	}

	def 'Saving routing preferences persists the changes'(){
		when:
			to PageGeneralSettings
		then:
			routing.useLastReceivedConnection.@checked
		when:
			routing.useLastReceivedConnection.click()
			routing.save.click()
		then:
			waitFor { at PageGeneralSettings }
			!routing.useLastReceivedConnection.@checked
	}
}
