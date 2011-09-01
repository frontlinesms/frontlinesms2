package frontlinesms2


class FolderSpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def setup() {
		controller = new FolderController()
	}

	def "can save new folder"() {
		setup:
			controller.params.name = "folder"
		when:
			controller.save()
			def folder = Folder.findByName("folder")
		then:
			folder
			folder.name == 'folder'
	}

}
