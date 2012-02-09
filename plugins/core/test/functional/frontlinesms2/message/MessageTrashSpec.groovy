package frontlinesms2.message

import frontlinesms2.*

class MessageTrashSpec extends grails.plugin.geb.GebSpec {
	
	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
	    	def m1 = new Fmessage(src: "src1", starred: true, deleted:true, inbound: true, date: new Date()).save(failOnError:true, flush:true)
	    	def m2 = new Fmessage(src: "src2", deleted:true, inbound: true, date: new Date()).save(failOnError:true, flush:true)
			Fmessage.findAll().collect{ new Trash(identifier:it.contactName, message:it.text, objectType:it.class.name, linkId:it.id).save(failOnError: true, flush: true)}
		when:
			to PageMessageTrash
		then:
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Starred').click()
			waitFor {$("#messages tbody tr").size() == 1}
		then:
			$("#messages tbody tr .message-preview-sender a").text() == 'src1'
		when:
			$('a', text:'All').click()
			waitFor {$("#messages tbody tr").size() == 2}
		then:
			$("#messages tbody tr .message-preview-sender a")*.text().containsAll(['src1', 'src2'])
	}
	
	def "should not contain export button" () {
		when:
			to PageMessageTrash
		then:
			!$('a', text:'Export')
	}
}
