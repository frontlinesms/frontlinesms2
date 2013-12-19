package frontlinesms2.folder

import frontlinesms2.*

class FolderBaseSpec extends grails.plugin.geb.GebSpec {
	void createTestFolders() {
		remote {
			['Work', 'Projects'].each() {
				Folder.build(name:it)
			}
			null
		}
	}

	void createTestMessages() {
		remote {
			def workFolder = Folder.findByName('Work')
			[TextMessage.build(src:'Max', text:'I will be late', date:new Date()-4, starred:true),
					TextMessage.build(src:'Jane', text:'Meeting at 10 am', date:new Date()-3)].each {
				workFolder.addToMessages(it)
			}
			workFolder.save(failOnError:true, flush:true)

			def projectsFolder = Folder.findByName('Projects')
			[TextMessage.build(src:'Patrick', text:'Project has started', date:new Date()-2),
					TextMessage.build(src:'Zeuss', text:'Sewage blocked', date:new Date()-1)].each {
				projectsFolder.addToMessages(it)
			}
			projectsFolder.save(failOnError:true, flush:true)
			null
		}
	}

	void createOutgoingMessage() {
		remote {
			def projectsFolder = Folder.findByName("Projects")
			def dis = new Dispatch(dst: '12345', status: DispatchStatus.PENDING)
			def m = new TextMessage(src:'Patrick', text:'Project has started', date:new Date()-2, inbound:false)
			m.addToDispatches(dis)
			projectsFolder.addToMessages(m)
			projectsFolder.save(failOnError:true, flush:true)
			null
		}
	}
}

