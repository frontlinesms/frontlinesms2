package frontlinesms2.folder

import frontlinesms2.*

class FolderGebSpec extends grails.plugin.geb.GebSpec {
	static createTestFolders() {
		[new Folder(value: 'Work'), 
			new Folder(value: 'Projects')].each() {
					it.save(failOnError:true, flush:true)
				}
	}

	static createTestMessages() {
		[new Fmessage(src:'Max', dst:'+254987654', text:'I will be late'),
				new Fmessage(src:'Jane', dst:'+2541234567', text:'Meeting at 10 am'),
				new Fmessage(src:'Patrick', dst:'+254112233', text:'Project has started'),
				new Fmessage(src:'Zeuss', dst:'+234234', text:'Sewage blocked')].each() {
					it.inbound = true
					it.save(failOnError:true, flush:true)
				}
		[Folder.findByValue('Work').addToMessages(Fmessage.findBySrc('Max')),
				Folder.findByValue('Work').addToMessages(Fmessage.findBySrc('Jane')),
				Folder.findByValue('Projects').addToMessages(Fmessage.findBySrc('Zeuss')),
				Folder.findByValue('Projects').addToMessages(Fmessage.findBySrc('Patrick'))].each() {
			it.save(failOnError:true, flush:true)
		}
	}

	static deleteTestFolders() {
		Folder.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

	static deleteTestMessages() {
		Fmessage.findAll().each() {
			it?.refresh()
			it?.delete(failOnError:true, flush:true)
		}
	}
}

