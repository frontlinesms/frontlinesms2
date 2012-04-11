package frontlinesms2.message

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
class MessageTrashSpec extends grails.plugin.geb.GebSpec {
	
	def "should filter inbox messages for starred and unstarred messages"() {
		setup:
	    	def m1 = new Fmessage(src: "src1", starred: true, isDeleted:true, inbound: true, date: new Date()).save(failOnError:true, flush:true)
	    	def m2 = new Fmessage(src: "src2", isDeleted:true, inbound: true, date: new Date()).save(failOnError:true, flush:true)
			Fmessage.findAll().collect{ new Trash(identifier:it.displayName, message:it.text, objectType:it.class.name, linkId:it.id).save(failOnError: true, flush: true)}
		when:
			to PageMessageTrash
		then:
			$("#message-list tr").size() == 3
		when:
			$('a', text:'Starred').click()
			waitFor {$("#message-list tr").size() == 2}
		then:
			getColumnText('message-list', 3) == ['src1']
		when:
			$('a', text:'All').click()
			waitFor {$("#message-list tr").size() == 3}
		then:
			getColumnText('message-list', 2).containsAll(['src1', 'src2'])
	}
	
	def "should not contain export button" () {
		when:
			to PageMessageTrash
		then:
			!$('a', text:'Export')
	}
}
