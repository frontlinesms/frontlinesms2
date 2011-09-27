package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ArchiveSpec extends IntegrationSpec {
	def controller
	
	def setup() {
		controller = new FolderController()
	}
	
	def "can archive a folder"() {
		setup:
			def folder = new Folder(name: 'rain').save(failOnError:true, flush:true)
		when:
			controller.params.id = folder.id
			println folder.id
			controller.archive()
		then:
			folder.archived == true
	}
}

