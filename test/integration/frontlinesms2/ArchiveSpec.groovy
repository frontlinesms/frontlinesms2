package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ArchiveSpec extends IntegrationSpec {
	def controller, pollcontroller

	def setup() {
		pollcontroller = new PollController()
		controller = new FolderController()
	}
	
	def "can archive a folder"() {
		given:
			def folder = new Folder(name: 'rain').save(failOnError:true, flush:true)
			assert !folder.archived
		when:
			controller.params.id = folder.id
			controller.archive()
		then:
			folder.refresh().archived
	}
	
	def "can unarchive a folder"() {
		given:
			def folder = new Folder(name: 'rain', archived: true).save(failOnError:true, flush:true)
			assert folder.archived
		when:
			controller.params.id = folder.id
			controller.unarchive()
		then:
			!folder.refresh().archived
	}
	
	def "can unarchive a poll"() {
		given:
			def poll = Poll.createPoll(title: 'thingy', choiceA:  'One', choiceB: 'Other', archived: true).save(failOnError:true, flush:true)
			assert poll.archived
		when:
			pollcontroller.params.id = poll.id
			pollcontroller.unarchive()
		then:
			!poll.refresh().archived
	}
}

