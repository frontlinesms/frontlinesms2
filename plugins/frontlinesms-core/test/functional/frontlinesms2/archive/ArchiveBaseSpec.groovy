package frontlinesms2.archive

import frontlinesms2.*

class ArchiveBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestFolders() {
		remote {
			['Work', 'Projects'].each() { Folder.build(name:it) }
			null
		}
	}

	def createTestMessages() {
		remote {
			Date TEST_DATE = new Date()

			def max = TextMessage.build(src:'Max', text:'I will be late', date:TEST_DATE-4, starred:true)
			def jane = TextMessage.build(src:'Jane', text:'Meeting at 10 am', date:TEST_DATE-3)
			def patrick = TextMessage.build(src:'Patrick', text:'Project has started', date:TEST_DATE-2)
			def zeuss = TextMessage.build(src:'Zeuss', text:'Sewage blocked', date:TEST_DATE-1)

			[Work:[max, jane], Projects:[zeuss, patrick]].each { folderName, messages ->
				Folder f = Folder.findByName(folderName)
				messages.each { f.addToMessages(it) }
				f.save(failOnError:true, flush:true)
			}
			null
		}
	}
	
	def createTestMessages2() {
		remote {
			Date TEST_DATE = new Date()

			TextMessage.build(src:'Max', text:'I will be late', date:TEST_DATE-4, archived:true)
			TextMessage.build(src:'Jane', text:'Meeting at 10 am', date:TEST_DATE-3, archived:true)
			null
		}
	}
}

