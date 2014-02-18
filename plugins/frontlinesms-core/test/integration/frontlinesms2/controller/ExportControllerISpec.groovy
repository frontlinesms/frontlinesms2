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
			def p = new Poll(name: 'Football Teams')
			p.editResponses(choiceA: 'manchester', choiceB:'barcelona')
			p.save(flush: true)
			[PollResponse.findByValue('manchester').addToMessages(TextMessage.findBySrc('Bob')),
					PollResponse.findByValue('manchester').addToMessages(TextMessage.findBySrc('Alice'))]*.save(failOnError:true, flush:true)
			controller.params.messageSection = "poll"
			controller.params.ownerId = Poll.findByName("Football Teams").id
		when:
			def result = controller.downloadMessageReport()
		then:
			result.interactionInstanceList.size() == 2
	}

	def "can export messages from a folder"() {
		given:
			createTestFolders()
			controller.params.messageSection = "folder"
			controller.params.ownerId = Folder.findByName("Work").id
		when:
			def result = controller.downloadMessageReport()
		then:
			result.interactionInstanceList.size() == 2
	}
	
	def "can export messages from an announcement"() {
		given:
			createTestAnnouncement()
			controller.params.messageSection = "activity"
			controller.params.ownerId = Announcement.findByName("Free Food").id
		when:
			def result = controller.downloadMessageReport()
		then:
			result.interactionInstanceList.size() == 1
	}
	
	def "can export all contacts"() {
		given:
			createTestContacts()
		when:
			def result = controller.downloadContactReport()
		then:
			result.contactInstanceList.size() == 3
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
			result.contactInstanceList.size() == 2
	}

	def "can export only sent messages from a folder"() {
		given:
			def workFolder = new Folder(name: 'Work')
			workFolder.addToMessages(TextMessage.build(src: "Bob", inbound: true, date: new Date()))
			TextMessage m = new TextMessage(text:"test", inbound:false, date:new Date())
			Dispatch d = new Dispatch(dst: '54321', status: DispatchStatus.PENDING)
			m.addToDispatches(d)
			m.save(failOnError:true)
			workFolder.addToMessages(m)
			workFolder.save()
			controller.params.messageSection = "folder"
			controller.params.ownerId = workFolder.id
			controller.params.inbound = false
		when:
			def result = controller.downloadMessageReport()
		then:
			result.interactionInstanceList.size() == 1
	}

	def "can export only received messages from a folder"() {
		given:
			def workFolder = new Folder(name: 'Work')
			workFolder.addToMessages(TextMessage.build(src: "Bob", inbound: true, date: new Date()))
			TextMessage m = new TextMessage(text:"test", inbound:false, date:new Date())
			Dispatch d = new Dispatch(dst: '54321', status: DispatchStatus.PENDING)
			m.addToDispatches(d)
			m.save(failOnError:true)
			workFolder.addToMessages(m)
			workFolder.save()
			controller.params.messageSection = "folder"
			controller.params.ownerId = workFolder.id
			controller.params.inbound = true
		when:
			def result = controller.downloadMessageReport()
		then:
			result.interactionInstanceList.size() == 1
	}

	def "can export only received messages from an activity"() {
		given:
			def a = new Announcement(name:'Free Food')
			a.addToMessages(TextMessage.build(src: "Bob", inbound: true, date: new Date()))
			TextMessage m = new TextMessage(text:"test", inbound:false, date:new Date())
			Dispatch d = new Dispatch(dst: '54321', status: DispatchStatus.PENDING)
			m.addToDispatches(d)
			m.save(failOnError:true)
			a.addToMessages(m)
			a.save(failOnError:true)
			controller.params.messageSection = "activity"
			controller.params.ownerId = a.id
			controller.params.inbound = true
		when:
			def result = controller.downloadMessageReport()
		then:
			result.interactionInstanceList.size() == 1
	}


	def createTestMessages() {
		[TextMessage.build(src:'Bob', text:'I like manchester', date: new Date() - 4, starred: true),
			TextMessage.build(src:'Alice', text:'go manchester', date: new Date() - 3)].each {
					it.inbound = true
					it.save(failOnError:true, flush:true)
			}
	}

	def createTestFolders() {
		def workFolder = new Folder(name: 'Work')
		workFolder.addToMessages(TextMessage.build(src: "Bob", inbound: true, date: new Date()))
		workFolder.addToMessages(TextMessage.build(src: "Alice", inbound: true, date: new Date()))
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
	
	def createTestAnnouncement() {
		TextMessage.build(src: "Bob", inbound: true, date: new Date())
		def a = new Announcement(name:'Free Food')
		a.addToMessages(TextMessage.build(src:"Alice", inbound:true, date:new Date())).save(failOnError:true, flush:true)
	}
}

