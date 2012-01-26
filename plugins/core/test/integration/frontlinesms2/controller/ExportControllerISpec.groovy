package frontlinesms2.controller

import frontlinesms2.*

class ExportControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller

	def setup() {
		createTestMessages()
		controller = new ExportController()
	}

	def "can export messages from a poll"() {
		given:
			Poll.createPoll(name: 'Football Teams', choiceA: 'manchester', choiceB:'barcelona').save(flush: true)
			[PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Bob')),
				PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Alice'))]*.save(failOnError:true, flush:true)
			controller.params.messageSection = "poll"
			controller.params.ownerId = Poll.findByName("Football Teams").id
		when:
			def result = controller.downloadMessageReport()
		then:
			result['messageInstanceList'].size() == 2
	}


	def "can export messages from a folder"() {
		given:
			createTestFolders()
			controller.params.messageSection = "folder"
			controller.params.ownerId = Folder.findByName("Work").id
		when:
			def result = controller.downloadMessageReport()
		then:
			result['messageInstanceList'].size() == 2
	}
	
	def "can export all contacts"() {
		given:
			createTestContacts()
		when:
			def result = controller.downloadContactReport()
		then:
			result['contactInstanceList'].size() == 3
	}
	
	def "can export contacts from a group"() {
		given:
			createTestContacts()
			createTestGroups()
		when:
			controller.params.contactsSection = 'group'
			controller.params.groupId = Group.findByName('Dwarves').id
			def result = controller.downloadContactReport()
		then:
			result['contactInstanceList'].size() == 2
	}


	def createTestMessages() {
		[new Fmessage(src:'Bob', text:'I like manchester', date: new Date() - 4, starred: true),
			new Fmessage(src:'Alice', text:'go manchester', date: new Date() - 3)].each {
					it.inbound = true
					it.save(failOnError:true, flush:true)
			}
	}

	def createTestFolders() {
		//FIXME: Need to remove.Test fails without this line.
		Folder.list()

		def workFolder = new Folder(name: 'Work')
		workFolder.addToMessages(new Fmessage(src: "Bob", inbound: true, date: new Date()))
		workFolder.addToMessages(new Fmessage(src: "Alice", inbound: true, date: new Date()))
		workFolder.save(flush: true)
	}

	def createTestContacts() {
		[new Contact(name:'Gimli'),
			new Contact(name: "Borthon"),
			new Contact(name: "Legolas")].each {
				it.save(failOnError: true, flush: true)
		}
	}
	
	def createTestGroups() {
		def dwarves = new Group(name: 'Dwarves').save(flush: true)
		dwarves.addToMembers(Contact.findByName('Gimli'))
		dwarves.addToMembers(Contact.findByName('Borthon'))
		dwarves.save(failOnError: true, flush: true)
	}
}
