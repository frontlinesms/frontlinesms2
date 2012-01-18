package frontlinesms2.message

import frontlinesms2.*

class MessageTrashSpec extends grails.plugin.geb.GebSpec {
	
	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
	    	def m1 = new Fmessage(src: "src1", dst: "dst1", starred: true, deleted:true).save(failOnError:true, flush:true)
	    	def m2 = new Fmessage(src: "src2", dst: "dst2", deleted:true).save(failOnError:true, flush:true)
			Fmessage.findAll().collect{ new Trash(identifier:it.contactName, message:it.text, objectType:it.class.name, linkId:it.id).save(failOnError: true, flush: true)}
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
			$("#messages tbody tr").collect {it.find("td:nth-child(3)").text()}.containsAll(['To:dst1', 'To:dst2'])
	}
	
	def "should not contain export button" () {
		when:
			to PageMessageTrash
		then:
			!$('a', text:'Export')
	}
}
