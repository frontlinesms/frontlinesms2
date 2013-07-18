package frontlinesms2.message

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
class MessageTrashSpec extends grails.plugin.geb.GebSpec {
	def trashService

	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
			remote {
				trashService = new TrashService()
				Fmessage.build(src:"src1", starred:true, isDeleted:true)
				Fmessage.build(src:"src2", isDeleted:true)
				Fmessage.findAll().each { trashService.sendToTrash(it) }
				null
			}
		when:
			to PageMessageTrash
		then:
			messageList.messageCount() == 2
		when:
			footer.showStarred.click()
			waitFor { messageList.messageCount() == 1 }
		then:
			messageList.messageSource(0) == "src1"
		when:
			footer.showAll.click()
			waitFor { messageList.messageCount() == 2 }
		then:
			messageList.messageSource(0) == 'src1'
			messageList.messageSource(1) == 'src2'
	}
	
	def "should not contain export button" () {
		when:
			to PageMessageTrash
		then:
			!header.export.displayed()
	}
}

