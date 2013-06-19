package frontlinesms2.controllers
import frontlinesms2.*

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

	def "folder names should be unique unless deleted or archived"() {
		new Folder(name: 'FOLDER').save()
		when:
			def folder1 = new Folder(name: "Folder")
			def folder2 = new Folder(name: "folder")
			def folder3 = new Folder(name: "FOLD")
		then:
			!folder1.validate()
			!folder2.validate()
			folder3.validate()
		when:
			folder3.archived
			def folder4 = new Folder(name: "fold")
		then:
			folder4.validate()
		when:
			folder4.deleted
			def folder5 = new Folder(name: "fold")
		then:
			folder5.validate()
	}
}

