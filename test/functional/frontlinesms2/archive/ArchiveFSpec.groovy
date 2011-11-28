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
			go "archive/inbox/show/${Fmessage.findBySrc('Max').id}?viewingArchive=true"
		then:
			getColumnText('message-list', 2) == ['Max', 'Jane']
		when:
			def btnDelete = $("#delete-msg")
			btnDelete.click()
		then:
			getColumnText('message-list', 2) == ['Jane']
	}
	
	def '"Archive All" button does not appear in archive section'() {
		given:
			new Fmessage(src:'Max', dst:'+254987654', text:'I will be late', dateReceived: new Date() - 4, archived:true, status:MessageStatus.INBOUND).save(flush:true)
			new Fmessage(src:'Jane', dst:'+2541234567', text:'Meeting at 10 am', dateReceived: new Date() - 3, archived:true, status:MessageStatus.INBOUND).save(flush:true)
		when:
			go "archive/inbox/show/${Fmessage.findBySrc('Max').id}?viewingArchive=true"
			$(".message-select")[0].click()
		then:
			!$("#btn_archive_all").displayed
		when:
			$(".message-select")[1].click()
			$(".message-select")[2].click()
		then:
			!$("#btn_archive_all").displayed
		
	}
}
