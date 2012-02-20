package frontlinesms2

import grails.plugin.spock.ControllerSpec
import groovy.lang.MetaClass;

class FolderControllerSpec extends ControllerSpec {
	def setup() {
		mockDomain(Folder)
		mockDomain(Fmessage)
		Fmessage.metaClass.static.owned = { Folder f, Boolean b, Boolean c ->
			Fmessage
		}
	}

	def "can archive a folder"() {
		given:
			def folder = new Folder(name: 'rain').save(failOnError:true)
			assert !folder.archived
		when:
			mockParams.id = folder.id
			controller.archive()
		then:
			folder.archived
	}
	
	def "can unarchive a folder"() {
		given:
			def folder = new Folder(name:'rain', archived:true).save()
			assert folder.archived
		when:
			mockParams.id = folder.id
			controller.unarchive()
		then:
			!folder.archived
	}
}

