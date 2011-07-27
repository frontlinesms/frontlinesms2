package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class SentListSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		new Fmessage(src:"src1", dst:"dst1",status:MessageStatus.SENT, starred:true).save(flush: true)
		new Fmessage(src:"src2", dst:"dst1",status:MessageStatus.SENT).save(flush: true)
	}

	def cleanup() {
		Fmessage.list().each {
			it.refresh()
			it.delete(flush: true)
		}
	}

	def "should filter folder messages for starred and unstarred messages"() {
		when:
			go "message/sent"
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'src1'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['src1', 'src2'])
	}
}
