package frontlinesms2.controller

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class ArchiveISpec extends IntegrationSpec {
	def folderController, pollController, archiveController

	def setup() {
		pollController = new PollController()
		folderController = new FolderController()
		archiveController = new ArchiveController()
	}
	
	def "deleted folders do not appear in the archive section"() {
		given:
			def folder = new Folder(name: 'rain', archived:true).save(failOnError:true, flush:true)
			assert folder.archived
		when:
			archiveController.folderList()
			def model = archiveController.modelAndView.model
		then:
			model.folderInstanceList == [folder]
		when:
			folder.deleted = true
			archiveController.folderList()
			model = archiveController.modelAndView.model
		then:
			!model.folderInstanceList
	}
	
	def "deleted polls do not appear in the archive section"() {
		given:
			def poll = Poll.createPoll(name: 'thingy', choiceA:  'One', choiceB: 'Other', archived: true).save(failOnError:true, flush:true)
			assert poll.archived
		when:
			archiveController.activityList()
			def model = archiveController.modelAndView.model
		then:
			model.activityInstanceList == [poll]
		when:
			poll.deleted = true
			archiveController.activityList()
			model = archiveController.modelAndView.model
		then:
			!model.activityInstanceList
	}
}

