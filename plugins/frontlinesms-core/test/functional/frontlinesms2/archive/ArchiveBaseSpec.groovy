package frontlinesms2.archive

import frontlinesms2.*

class ArchiveBaseSpec extends grails.plugin.geb.GebSpec {
	final Date TEST_DATE = new Date()

	def createTestFolders() {
		['Work', 'Projects'].each() { Folder.build(name:it) }
	}

	def createTestMessages() {
		def max = Fmessage.build(src:'Max', text:'I will be late', date:TEST_DATE-4, starred:true)
		def jane = Fmessage.build(src:'Jane', text:'Meeting at 10 am', date:TEST_DATE-3)
		def patrick = Fmessage.build(src:'Patrick', text:'Project has started', date:TEST_DATE-2)
		def zeuss = Fmessage.build(src:'Zeuss', text:'Sewage blocked', date:TEST_DATE-1)

		[Work:[max, jane], Projects:[zeuss, patrick]].each { folderName, messages ->
			Folder f = Folder.findByName(folderName)
			messages.each { f.addToMessages(it) }
			f.save(failOnError:true, flush:true)
		}
	}
	
	def createTestMessages2() {
		Fmessage.build(src:'Max', text:'I will be late', date:TEST_DATE-4, archived:true)
		Fmessage.build(src:'Jane', text:'Meeting at 10 am', date:TEST_DATE-3, archived:true)
	}
}

