package frontlinesms2.archive

import frontlinesms2.*

class ArchiveFSpec extends ArchiveBaseSpec {
	
	def 'archived folder list is displayed'() {
		given:
			createTestFolders()
			createTestMessages()
		when:
			def folder = Folder.findByName('Work')
			folder.archive()
			folder.save(flush: true, failOnError: true)
			to PageArchiveFolder
		then:
			folderNames*.text() == ["Work"]
	}
}
