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
	
	def "deleted polls do not appear in the archive section"() {
		given:
			def poll = new Poll(name: 'thingy', archived: true)
			poll.editResponses(choiceA: 'One', choiceB: 'Other')
			poll.save(failOnError:true, flush:true)
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

