package frontlinesms2.controllers

import frontlinesms2.*

import spock.lang.*
import groovy.lang.MetaClass;
import grails.buildtestdata.mixin.Build

@TestFor(ArchiveController)
@Mock([Folder, Fmessage, Autoforward, Contact])
@Build([Contact, Autoforward])

class ArchiveControllerSpec extends Specification {
	def "deleted folders do not appear in the archive section"() {
		given:
			def folder = new Folder(name:'rain', archived:true).save()
			assert folder.archived
		when:
			controller.folderList()
		then:
			model.folderInstanceList == [folder]
		when:
			folder.deleted = true
			folder.save()
			controller.folderList()
		then:
			!model.folderInstanceList
	}

	def "the autoforward action should redirect to the activity action"() {
		given:
			def autoforward = Autoforward.build(contacts:[Contact.build(mobile:"+2345678")], sentMessageText:'some forward text')
		when:
			params.messageSection = "activity"
			params.ownerId = autoforward.id
			controller.autoforward()
		then:
			response.redirectUrl == "/archive/activity?messageSection=activity&ownerId=${autoforward.id}"
			
	}
}

