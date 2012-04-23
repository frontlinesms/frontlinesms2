package frontlinesms2.archive

import frontlinesms2.*

class ArchiveBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestFolders() {
		['Work', 'Projects'].each() { Folder.build(name:it) }
	}

	def createTestMessages() {
		Fmessage.build(src:'Max', text:'I will be late', dateReceived:new Date()-4, starred:true)
		Fmessage.build(src:'Jane', text:'Meeting at 10 am', dateReceived:new Date()-3)
		Fmessage.build(src:'Patrick', text:'Project has started', dateReceived:new Date()-2)
		Fmessage.build(src:'Zeuss', text:'Sewage blocked', dateReceived: new Date()-1)

		[Folder.findByName('Work').addToMessages(Fmessage.findBySrc('Max')),
				Folder.findByName('Work').addToMessages(Fmessage.findBySrc('Jane')),
				Folder.findByName('Projects').addToMessages(Fmessage.findBySrc('Zeuss')),
				Folder.findByName('Projects').addToMessages(Fmessage.findBySrc('Patrick'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}
	
	def createTestMessages2() {
		Fmessage.build(src:'Max', text:'I will be late', date:TEST_DATE-4, archived:true)
		Fmessage.build(src:'Jane', text:'Meeting at 10 am', date:TEST_DATE-3, archived:true)
	}
}

