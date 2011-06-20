package frontlinesms2

class SearchControllerIntegrationSpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def firstContact, secondContact
	def group
	def folder

	def setup() {
		controller = new SearchController()
		firstContact = new Contact(name:'Alex', address:'+254987654').save(failOnError:true)
		secondContact = new Contact(name:'Mark', address:'+254333222').save(failOnError:true)
		group = new Group(name:'test').save(failOnError:true)
		
		[new Fmessage(src:'+254987654', dst:'+254987654', text:'work at 11.00'),
			new Fmessage(src:'+254111222', dst:'+254937634', text:'work is awesome'),
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Michael', dst:'+2541234567', text:'Can we get meet in 5 minutes')].each() {
					it.inbound = true
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', inbound:true)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver', inbound:false)
		def liverMessage2 = new Fmessage(src:'+254333222', dst:'+12345678', text:'liver for lunch?', inbound:false)
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		liverResponse.addToMessages(liverMessage2)
		chickenResponse.addToMessages(chickenMessage)
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
	}

	def cleanup() {
		Group.findAll()*.delete(flush:true, failOnError:true)
		Contact.findAll()*.delete(flush:true, failOnError:true)
		Poll.findAll()*.delete(flush:true, failOnError:true)
		MessageOwner.findAll()*.delete(flush:true, failOnError:true)
		Fmessage.findAll()*.delete(flush:true, failOnError:true)
	}
	
	private def makeGroupMember() {
		firstContact.addToGroups(group, true)
		assert(Contact.get(firstContact.id).isMemberOf(Group.get(group.id)))
		secondContact.addToGroups(group, true)
		assert(Contact.get(secondContact.id).isMemberOf(Group.get(group.id)))
	}
	
	def "blank search does not return a list of messages"() {
		when:
			controller.params.keywords = ""
			controller.search()
			def model = controller.modelAndView.model.messageInstanceList
		then:
			!model
	}
	
	def "message searches can be restricted to a poll or folders"() {
		when:
			controller.params.keywords = "chicken"
			controller.params.activityId = "poll-${Poll.findByTitle('Miauow Mix').id}"
			controller.search()
			def model = controller.modelAndView.model.messageInstanceList
		then:
			model == [Fmessage.findBySrc('Barnabus')]
		when:
			new MessageOwner(value: 'work').save(failOnError: true, flush:true)
			folder = new Folder(value: 'work')
			folder.addToMessages(Fmessage.findBySrc('+254111222')).save(failOnError: true, flush:true)
			controller.params.keywords = "work"
			controller.params.activityId = "folder-${folder.id}"
			controller.search()
			def model2 = controller.modelAndView.model.messageInstanceList
		then:
			model2 == [Fmessage.findBySrc('+254111222')]
	}
	
	def "message searches can be restricted to a contact group"() {
		given:
			makeGroupMember()
		when:
			controller.params.keywords = "liver"
			controller.params.groupId = Group.findByName('test').id
			controller.search()
			def model = controller.modelAndView.model.messageInstanceList
		then:
			model == [ Fmessage.findBySrc('+254333222')]
	}
	
	def "groups without contacts do not return messages"() {
		when:
			controller.params.keywords = "test"
			controller.params.groupId = Group.findByName('test').id
			controller.search()
			def model = controller.modelAndView.model.messageInstanceList
		then:
			!model
	}
	
	def "message searches can be restricted to both contact groups and polls"() {
		given:
			makeGroupMember()
		when:
			controller.params.keywords = "liver"
			controller.params.activityId = "poll-${Poll.findByTitle('Miauow Mix').id}"
			controller.params.groupId = Group.findByName('test').id
			controller.search()
			def model = controller.modelAndView.model.messageInstanceList
		then:
			model == [Fmessage.findBySrc('+254333222')]
	}
	
	def "deleted messages do not appear in search results"() {
		when:
			controller.params.keywords = "liver"
			controller.params.activityId = "poll-${Poll.findByTitle('Miauow Mix').id}"
			Fmessage.findBySrc("+254333222").toDelete().save(flush: true)
			controller.search()
			def model = controller.modelAndView.model.messageInstanceList
		then:
			model == [Fmessage.findBySrc('Minime')]
	}
}