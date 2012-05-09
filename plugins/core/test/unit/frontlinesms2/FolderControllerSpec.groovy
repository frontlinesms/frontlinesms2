package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
 
@TestFor(FolderController)
@Mock([Folder, Fmessage])
class FolderControllerSpec extends Specification {
	def "can archive a folder"() {
		given:
			def folder = new Folder(name: 'rain').save(failOnError:true)
			assert !folder.archived
		when:
			params.id = folder.id
			controller.archive()
		then:
			folder.archived
	}
	
	def "can unarchive a folder"() {
		given:
			def folder = new Folder(name:'rain', archived:true).save()
			assert folder.archived
		when:
			params.id = folder.id
			controller.unarchive()
		then:
			!folder.archived
	}
}

