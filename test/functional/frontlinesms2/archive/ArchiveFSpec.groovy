package frontlinesms2.archive

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
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
	
	def 'should show list of remaining messages when a message is deleted'() {
		given:
			new Fmessage(src:'Max', dst:'+254987654', text:'I will be late', dateReceived: new Date() - 4, archived:true, status:MessageStatus.INBOUND).save(flush:true)
			new Fmessage(src:'Jane', dst:'+2541234567', text:'Meeting at 10 am', dateReceived: new Date() - 3, archived:true, status:MessageStatus.INBOUND).save(flush:true)
		when:
			go "archive/inbox/show/${Fmessage.findBySrc('Max').id}"
		then:
			getColumnText('messages', 2) == ['Jane', 'Max']
		when:
			def btnDelete = $("#message-delete")
			btnDelete.click()
		then:
			getColumnText('messages', 2) == ['Jane']
	}
}
