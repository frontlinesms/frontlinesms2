package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ArchiveSpec extends IntegrationSpec {
	def "can archive a folder"() {
		given:
			def controller = new FolderController()
			def folder = new Folder(name: 'rain').save(failOnError:true, flush:true)
			assert !folder.archived
		when:
			controller.params.id = folder.id
			controller.archive()
		then:
			folder.refresh().archived
	}
}

