package frontlinesms2.message

import frontlinesms2.*

class TrashListSpec extends frontlinesms2.poll.PollGebSpec {
	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
	    	new Fmessage(src: "src1", dst: "dst1", deleted: true, starred: true).save(flush: true)
	    	new Fmessage(src: "src2", dst: "dst1", deleted: true).save(flush: true)
		when:
			go "message/trash"
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr")[0].find("td:nth-child(2)").text() == 'src1'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr").collect {it.find("td:nth-child(2)").text()}.containsAll(['src1', 'src2'])
		cleanup:
			deleteTestMessages()
	}
}



