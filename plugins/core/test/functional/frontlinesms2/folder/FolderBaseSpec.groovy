package frontlinesms2.folder

import frontlinesms2.*

class FolderBaseSpec extends grails.plugin.geb.GebSpec {
	
	def createTestFolders() {
		['Work', 'Projects'].each() {
			new Folder(name:it).save(failOnError:true, flush:true)
		}
	}

	def createTestMessages() {
		[new Fmessage(src:'Max', text:'I will be late', date: new Date() - 4, starred: true),
				new Fmessage(src:'Jane', text:'Meeting at 10 am', date: new Date() - 3),
				new Fmessage(src:'Patrick', text:'Project has started', date: new Date() - 2),
				new Fmessage(src:'Zeuss', text:'Sewage blocked', date: new Date() - 1)].each() {
			it.inbound = true
			it.save(failOnError:true, flush:true)
		}
		[Folder.findByName('Work').addToMessages(Fmessage.findBySrc('Max')),
				Folder.findByName('Work').addToMessages(Fmessage.findBySrc('Jane')),
				Folder.findByName('Projects').addToMessages(Fmessage.findBySrc('Zeuss')),
				Folder.findByName('Projects').addToMessages(Fmessage.findBySrc('Patrick'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}
}

