package frontlinesms2.archive

import frontlinesms2.*

class ArchiveSpec extends frontlinesms2.archive.ArchiveGebSpec {
	
	def 'archived folder list is displayed'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			def folder = Folder.findByName('Work')
			folder.archiveFolder()
			folder.save(flush: true, failOnError: true)
			to ArchiveFolderPage
		then:
			folderNames*.text() == ["Work"]
	}
}
