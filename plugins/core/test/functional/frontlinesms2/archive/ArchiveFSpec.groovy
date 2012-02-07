package frontlinesms2.archive

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
class ArchiveFSpec extends ArchiveBaseSpec {
	final Date TEST_DATE = new Date()
	
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
			createTestMessages2()
		when:
			go "archive/inbox/show/${Fmessage.findBySrc('Max').id}?viewingArchive=true"
		then:
			$("#messages tbody tr").collect {it.find(".message-preview-sender").text()}.containsAll(['Jane', 'Max'])
		when:
			def btnDelete = $("#delete-msg")
			btnDelete.click()
		then:
			$("#messages tbody tr").collect {it.find(".message-preview-sender").text()}.containsAll(['Jane'])
	}
	
	def '"Archive All" button does not appear in archive section'() {
		given:
			createTestMessages2()
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
	
	def '"Delete All" button appears when multiple messages are selected in an archived activity'() {
		given:
			def poll = Poll.createPoll(name:'thingy', choiceA:'One', choiceB:'Other').save(failOnError:true, flush:true)
			def messages = createTestMessages2()
			poll.addToMessages(messages[0]).save(failOnError:true, flush:true)
			poll.addToMessages(messages[1]).save(failOnError:true, flush:true)
			poll.archive()
			poll.save(failOnError:true, flush:true)
		when:
			go "archive/poll/${poll.id}"
			$(".message-select")[0].click()
		then:
			waitFor { $("#btn_delete_all").displayed }
	}
	
	private def createTestMessages2() {
		[new Fmessage(src:'Max', text:'I will be late', date:TEST_DATE-4, archived:true, inbound:true).save(failOnError:true, flush:true),
			new Fmessage(src:'Jane', text:'Meeting at 10 am', date:TEST_DATE-3, archived:true, inbound:true).save(failOnError:true, flush:true)]
	}
}
