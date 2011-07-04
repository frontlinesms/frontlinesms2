package frontlinesms2.folder

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class FolderGebSpec extends grails.plugin.geb.GebSpec {
	static createTestFolders() {
		[new Folder(name: 'Work'), 
				new Folder(name: 'Projects')].each() {
			it.save(failOnError:true, flush:true)
		}
	}

	static createTestMessages() {
		[new Fmessage(src:'Max', dst:'+254987654', text:'I will be late', dateReceived: new Date() - 4, starred: true),
				new Fmessage(src:'Jane', dst:'+2541234567', text:'Meeting at 10 am', dateReceived: new Date() - 3),
				new Fmessage(src:'Patrick', dst:'+254112233', text:'Project has started', dateReceived: new Date() - 2),
				new Fmessage(src:'Zeuss', dst:'+234234', text:'Sewage blocked', dateReceived: new Date() - 1)].each() {
			it.status = MessageStatus.INBOUND
			it.save(failOnError:true, flush:true)
		}
		[Folder.findByName('Work').addToMessages(Fmessage.findBySrc('Max')),
				Folder.findByName('Work').addToMessages(Fmessage.findBySrc('Jane')),
				Folder.findByName('Projects').addToMessages(Fmessage.findBySrc('Zeuss')),
				Folder.findByName('Projects').addToMessages(Fmessage.findBySrc('Patrick'))].each() {
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

