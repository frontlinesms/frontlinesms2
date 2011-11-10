package frontlinesms2.message

import frontlinesms2.*

class MessageSentSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		new Fmessage(src:"src1", dst:"dst1",status:MessageStatus.SENT, starred:true).save(failOnError:true, flush: true)
		new Fmessage(src:"src2", dst:"dst2",status:MessageStatus.SENT).save(failOnError:true, flush: true)
	}

	def "can filter messages by starred and unstarred messages"() {
		when:
			go "message/sent"
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(3)").text() == 'dst1'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['dst1', 'dst2'])
	}
}
