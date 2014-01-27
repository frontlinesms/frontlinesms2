package frontlinesms2.settings

import frontlinesms2.*
import frontlinesms2.popup.ExportDialog
import frontlinesms2.contact.*

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

	def 'importing a contact csv takes the user to a review page'() {
		setup:
			def filePath = createTestUpload()
		when:
			to PageImportExportSettings
			importOption("contacts.csv").click()
			uploadCsv(filePath)
		then:
			waitFor { at PageImportReview }
	}

	def 'review page shows contact data matching imported contacts'() {
		when:
			skipToReview()
		then:
			[["Name","Mobile Number","Email","Notes","Group(s)"],
			["Kama","+123456789",'','',""],
			["Vernerck","+447944888888",'','',""],
			["Joe-Free","+254701000000",'','',""]].eachWithIndex { rowValue, rowIndex ->
				rowValue.eachWithIndex { cellValue, cellIndex ->
					assert valueAt(cellIndex, rowIndex) == cellValue
				}
			}
	}

	def 'submitting data on te review page completes contact import'() {
		when:
			skipToReview()
			submitButton.click()
		then:
			waitFor {
				at PageContactShow
			}
			remote {
				Contact.count() == 3
			}
	}

	private def createTestUpload(csvContent=null) {
		def csvString = csvContent?:"""\
"Name","Mobile Number","Email","Notes","Group(s)"
"Kama","+123456789",,,""
"Vernerck","+447944888888",,,""
"Joe-Free","+254701000000",,,""
		"""
		def file = new File(ResourceUtils.resourceDirectory, 'test_contact_import.csv')
		if (file.exists()) file.delete()
		file.write(csvString)
		file.absolutePath
	}

	private def skipToReview() {
		to PageImportExportSettings
		importOption("contacts.csv").click()
		uploadCsv(createTestUpload())
		waitFor { at PageImportReview }
	}
}

