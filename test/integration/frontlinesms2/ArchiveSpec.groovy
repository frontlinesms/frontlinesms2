package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ArchiveSpec extends IntegrationSpec {
	def controller
	def folder 
	
	def setup() {
		controller = new FolderController()
		folder = new Folder(name: 'rain').save(failOnError:true, flush:true)
	}
	
	def "can archive a folder"() {
		when:
			assert !folder.archived
			controller.params.id = folder.id
			controller.archive()
		then:
			folder.refresh().archived
	}
}

