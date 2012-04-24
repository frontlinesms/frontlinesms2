package frontlinesms2.folder

import frontlinesms2.*

class FolderBaseSpec extends grails.plugin.geb.GebSpec {
	
	def createTestFolders() {
		['Work', 'Projects'].each() {
			Folder.build(name:it)
		}
	}

	def createTestMessages() {
		def workFolder = Folder.findByName('Work')
		[Fmessage.build(src:'Max', text:'I will be late', date:new Date()-4, starred:true),
				Fmessage.build(src:'Jane', text:'Meeting at 10 am', date:new Date()-3)].each {
			workFolder.addToMessages(it)
		}
		workFolder.save(failOnError:true, flush:true)

		def projectsFolder = Folder.findByName('Projects')
		[Fmessage.build(src:'Patrick', text:'Project has started', date:new Date()-2),
				Fmessage.build(src:'Zeuss', text:'Sewage blocked', date:new Date()-1)].each {
			projectsFolder.addToMessages(it)
		}
		projectsFolder.save(failOnError:true, flush:true)
	}
}

