package frontlinesms2.archive

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.poll.*
import frontlinesms2.message.PageMessageInbox

@Mixin(frontlinesms2.utils.GebUtil)
class ArchiveFSpec extends ArchiveBaseSpec {
	def 'archived folder list is displayed'() {
		given:
			createTestFolders()
			createTestMessages()
			remote {
				def folder = Folder.findByName('Work')
				folder.archive()
				folder.save(flush:true, failOnError:true)
				null
			}
		when:
			to PageArchiveFolder
		then:
			folderNames*.text() == ["Work"]
	}

	def 'should show list of remaining messages when a message is deleted'() {
		given:
			createTestMessages2()
		when:
			to PageArchiveInbox, remote { Fmessage.findBySrc('Max').id }
		then:
			messageList.messageCount == 2
			messageList.messageSource(0) == 'Jane'
			messageList.messageSource(1) == 'Max'
		when:
			to PageArchiveInbox, remote { Fmessage.findBySrc('Max').id }
			singleMessageDetails.delete.click()
		then:
			messageList.messageCount == 1
			messageList.messageSource == 'Jane'
	}

	def '"Archive All" button does not appear in archive section'() {
		given:
			createTestMessages2()
		when:
			to PageArchiveInbox, remote { Fmessage.findBySrc('Max').id }
			messageList.selectAll.click()
		then:
			!multipleMessageDetails.archiveAll.displayed
	}

	def '"Delete All" button appears when multiple messages are selected in an archived activity'() {
		given:
			remote {
				Date TEST_DATE = new Date()
				def poll = new Poll(name:'thingy')
				poll.addToResponses(key:'A', value:'One')
				poll.addToResponses(key:'B', value:'Other')
				poll.addToResponses(PollResponse.createUnknown())
				poll.save(failOnError:true, flush:true)
				def messages = [Fmessage.build(src:'Max', text:'I will be late', date:TEST_DATE-4),
						Fmessage.build(src:'Max', text:'I will be late', date:TEST_DATE-4)] 
				println messages
				poll.addToMessages(messages[0])
				poll.addToMessages(messages[1])
				poll.save(failOnError:true, flush:true)
				poll.archive()
				poll.save(flush:true)
				poll.refresh()
				assert poll.activityMessages.every { it.archived }
			}
		when:
			to PageMessagePoll, 'thingy'
			messageList.selectAll.click()
		then:
			waitFor { multipleMessageDetails.deleteAll.displayed }
	}
}

