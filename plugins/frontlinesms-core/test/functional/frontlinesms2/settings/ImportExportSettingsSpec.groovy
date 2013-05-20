package frontlinesms2.settings

import frontlinesms2.*
import frontlinesms2.popup.ExportDialog

class ImportExportSettingsSpec extends grails.plugin.geb.GebSpec {
	def "all contacts popup should appear when export button is clicked"(){
		when:
			to PageImportExportSettings
			exportOption("allcontacts").click()
			exportButton.click()
		then:
			waitFor { at ExportDialog }
	}

	def 'inbox export popup shoulld appear when export button is clicked'(){
		when:
			to PageImportExportSettings
			exportOption("inboxmessages").click()
			exportButton.click()
		then:
			waitFor { at ExportDialog }
	}
}

