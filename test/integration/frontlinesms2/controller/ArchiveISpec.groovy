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
	
	def "can archive a folder"() {
		given:
			def folder = new Folder(name: 'rain').save(failOnError:true, flush:true)
			assert !folder.archived
		when:
			folderController.params.id = folder.id
			folderController.archive()
		then:
			folder.refresh().archived
	}
	
	def "can unarchive a folder"() {
		given:
			def folder = new Folder(name:'rain', archived:true).save(failOnError:true, flush:true)
			assert folder.archived
		when:
			folderController.params.id = folder.id
			folderController.unarchive()
		then:
			!folder.refresh().archived
	}
	
	def "can unarchive a poll"() {
		given:
			def poll = Poll.createPoll(title:'thingy', choiceA:'One', choiceB:'Other', archived:true).save(failOnError:true, flush:true)
			assert poll.archived
		when:
			pollController.params.id = poll.id
			pollController.unarchive()
		then:
			!poll.refresh().archived
	}
	
	def "deleted folders do not appear in the archive section"() {
		given:
			def folder = new Folder(name: 'rain', archived:true).save(failOnError:true, flush:true)
			assert folder.archived
		when:
			def model = archiveController.folderView()
		then:
			model.folderInstanceList == [folder]
		when:
			folder.deleted = true
			model = archiveController.folderView()
		then:
			!model.folderInstanceList
	}
	
	def "deleted polls do not appear in the archive section"() {
		given:
			def poll = Poll.createPoll(title: 'thingy', choiceA:  'One', choiceB: 'Other', archived: true).save(failOnError:true, flush:true)
			assert poll.archived
		when:
			archiveController.activityView()
			def model = archiveController.modelAndView.model
		then:
			model.pollInstanceList == [poll]
		when:
			poll.deleted = true
			archiveController.activityView()
			model = archiveController.modelAndView.model
		then:
			!model.pollInstanceList
	}
}

