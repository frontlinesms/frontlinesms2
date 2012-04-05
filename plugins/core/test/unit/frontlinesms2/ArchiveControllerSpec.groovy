package frontlinesms2

import grails.plugin.spock.ControllerSpec
import groovy.lang.MetaClass;

class ArchiveControllerSpec extends ControllerSpec {
	def setup() {
		mockDomain(Folder)
		mockDomain(Poll)
		mockDomain(Fmessage)
		registerMetaClass(Fmessage)
		Fmessage.metaClass.static.owned = { Folder f, Boolean b, Boolean c ->
			Fmessage
		}
	}

	def "deleted folders do not appear in the archive section"() {
		given:
			def folder = new Folder(name: 'rain', archived:true).save()
			assert folder.archived
		when:
			controller.folderList()
			def model = controller.modelAndView.model
		then:
			model.folderInstanceList == [folder]
		when:
			folder.deleted = true
			controller.folderList()
			model = controller.modelAndView.model
		then:
			!model.folderInstanceList
	}
	
	def "deleted polls do not appear in the archive section"() {
		given:
			def poll = new Poll(name: 'thingy', archived: true)
			poll.editResponses(choiceA: 'One', choiceB: 'Other')
			poll.save()
			assert poll.archived
		when:
			archiveController.activityList()
			def model = controller.renderArgs.model
		then:
			model.activityInstanceList == [poll]
		when:
			poll.deleted = true
			archiveController.activityList()
			model = controller.renderArgs.model
		then:
			!model.activityInstanceList
	}
}

