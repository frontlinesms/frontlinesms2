package frontlinesms2.message

import frontlinesms2.*

class MessageTrashSpec extends grails.plugin.geb.GebSpec {
	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
	    	new Fmessage(src: "src1", dst: "dst1", deleted: true, starred: true).save(flush: true).toDelete()
	    	new Fmessage(src: "src2", dst: "dst2", deleted: true).save(flush: true).toDelete()
		when:
			to PageMessageTrash
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
	
	def "should not contain export button" () {
		when:
			to PageMessageTrash
		then:
			!$('a', text:'Export')
	}
}



