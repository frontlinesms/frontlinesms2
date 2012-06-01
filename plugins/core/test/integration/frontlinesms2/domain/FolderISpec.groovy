package frontlinesms2.domain

import frontlinesms2.*

class FolderISpec extends grails.plugin.spock.IntegrationSpec {

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
	
	def "A folder can be archvied"() {
		when:
			def f = new Folder(name:'test').save(failOnError:true)
		then:
			f.archived == false
		when:
			f.archive()
			f.save(failOnError:true, flush: true)
		then:
			f.archived == true
	}
	
	def "When a folder is archived all of its messages are archived"() {
		setup:
			def f = Folder.build(name:'test')
			def m = Fmessage.build(inbound: true)
		when:
			f.addToMessages(m)
			f.save()
		then:
			m.messageOwner == f
			f.archived == false
			m.archived == false
		when:
			f.archive()
			m.refresh()
		then:
			f.archived == true
			m.archived == true
	}
	
	def "Adding a message to a Folder will cascade to the message's activity value"() {
		given:
			def f = new Folder(name:'test').save(failOnError:true)
			def m = new Fmessage(date: new Date(), inbound: true, src: 'src')
		when:
			f.addToMessages(m)
			f.save(failOnError:true)
		then:
			m.messageOwner == f
	}

	def 'folder name must be unique'(){
		given:
			controller.params.name == 'folder'
			controller.save()
		when:
			def f = new Folder(name:'folder').save(flush:true)
		then:
			Folder.findByName("folder").count() == 1
			controller.flash.message == 'Failed to create Folder'
	}
}
