package frontlinesms2.controller

import java.text.SimpleDateFormat
import frontlinesms2.*

class SearchControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def firstContact, secondContact, thirdContact
	def group
	def folder


	def setup() {
		controller = new SearchController()
		firstContact = new Contact(name:'Alex', primaryMobile:'+254987654').save(failOnError:true)
		secondContact = new Contact(name:'Mark', primaryMobile:'+254333222').save(failOnError:true)
		thirdContact = new Contact(name:"Toto", primaryMobile:'+666666666').save(failOnError:true)
		group = new Group(name:'test').save(failOnError:true)
		new Group(name:'nobody').save(failOnError:true, flush:true )
		
		//message in the same day will still be return even if in the future
		def futureDate = new Date()
		futureDate.hours = futureDate.hours + 1
		
		[new Fmessage(src:'+254987654', dst:'+254987654', text:'work at 11.00', archived: true, date: new Date()),
				new Fmessage(src:'+254987654', dst:'+6645666666', text:'finaly i stay in bed', date: new Date()),
				//new Fmessage(src:'+666666666', dst:'+254987654', text:'finaly i stay in bed', date: new Date()),
				new Fmessage(src:'+254111222', dst:'+254937634', date: futureDate, text:'work is awesome'),
				new Fmessage(src:'Bob', dst:'+254987654', date: new Date()-5, text:'hi Bob'),
				new Fmessage(src:'Michael', dst:'+2541234567', date: new Date()-7,text:'Can we get meet in 5 minutes')].each() {
			it.inbound = true
			it.save(failOnError:true)
		}
				
		[new CustomField(name:'city', value:'Paris', contact: firstContact),
				new CustomField(name:'like', value:'cake', contact: secondContact),
				new CustomField(name:'ik', value:'car', contact: secondContact),
				new CustomField(name:'like', value:'ake', contact: thirdContact),
				new CustomField(name:'dob', value:'12/06/79', contact: secondContact),
				new Fmessage(src:'+666666666', dst:'+2549', text:'finaly i stay in bed', inbound:true, date: new Date())].each {
			it.save(failOnError:true)
		}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', inbound:true, date: new Date()).save(failOnError:true)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver', inbound:true, date: new Date()).save(failOnError:true)
		def liverMessage2 = new Fmessage(src:'+254333222', dst:'+12345678', text:'liver for lunch?', inbound:true, date: new Date()).save(failOnError:true)
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		liverResponse.addToMessages(liverMessage2)
		chickenResponse.addToMessages(chickenMessage)
		def poll = new Poll(title:'Miauow Mix')
		poll.addToResponses(chickenResponse)
		poll.addToResponses(liverResponse).save(failOnError:true)
	}
	
	private def makeGroupMember() {
		firstContact.addToGroups(group)
		secondContact.addToGroups(group)
		firstContact.save(failOnError:true)
		secondContact.save(failOnError:true)
		assert(Contact.get(firstContact.id).isMemberOf(Group.get(group.id)))
		assert(Contact.get(secondContact.id).isMemberOf(Group.get(group.id)))
	}
	
	def "blank search string returns a list of messages"() {
		when:
			def search = new Search(name: "toSave", searchString: "")
			search.save(failOnError: true, flush: true)
			controller.params.search = search
			controller.params.inArchive = true
			def model = controller.result()
		then:
			model.messageInstanceList.size() == 9
	}

	def "message searches can be restricted to a poll"() {
		when:
			controller.params.searchString = "chicken"
			controller.params.activityId = "poll-${Poll.findByTitle('Miauow Mix').id}"
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('Barnabus')]
	}

//FIXME	
//	def "message searches can be restricted to a folder"() {
//		given:
//			folder = new Folder(name: 'work').save(failOnError:true, flush:true)
//			def m = Fmessage.findBySrc('+254111222')
//			folder.addToMessages(m).save(failOnError: true, flush:true)
//			m.save(flush:true, failOnError:true)
//		when:
//			controller.params.searchString = "work"
//			controller.params.activityId = "folder-${folder.id}"
//			def model = controller.result()
//		then:
//			model.messageInstanceList == [Fmessage.findBySrc('+254111222')]
//	}
	
	def "search for inbound messages only"() {
		when:
			controller.params.messageStatus = "INBOUND"
			controller.params.inArchive = true
			def model = controller.result()
		then:
			model.messageInstanceTotal == 9
			model.messageInstanceList.every { it.inbound }
	}

	@spock.lang.IgnoreRest
	def "search for sent messages only"() {
		setup:
			new Fmessage(src:"src", dst:"dst", hasPending:true, date: new Date()).save(flush:true)
			new Fmessage(src:"src", dst:"dst", hasSent:true, date: new Date()).save(flush:true)
			new Fmessage(src:"src", dst:"dst", hasFailed:true, date: new Date()).save(flush:true)
		when:
			controller.params.messageStatus = "SENT, PENDING, FAILED"
			def model = controller.result()
			println model.messageInstanceList.collect { [hasSent:it.hasSent, inbound:it.inbound, hasPending:it.hasPending, hasFailed:it.hasFailed] }
		then:
			model.messageInstanceList.size() == 3
			model.messageInstanceList.every { !it.inbound }
	}

	def "message searches can be restricted to a contact group, and choice is still present after search completes"() {
		given:
			makeGroupMember()
		when:
			controller.params.searchString = "liver"
			controller.params.groupId = Group.findByName('test').id
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('+254333222')]
			controller.params.groupId == Group.findByName('test').id
	}
	
	def "message searches in a group with no member return empty list"(){
		when:
			controller.params.groupId = Group.findByName('nobody').id
			def model = controller.result()
		then:
			model.messageInstanceList == []
	}
	
	def "message searches can be restricted to both contact groups and polls"() {
		given:
			makeGroupMember()
		when:
			controller.params.searchString = "liver"
			controller.params.activityId = "poll-${Poll.findByTitle('Miauow Mix').id}"
			controller.params.groupId = Group.findByName('test').id
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('+254333222')]
	}
	
	def "message searches can be restricted to individual contacts"() {
		when:
			controller.params.contactString = "alex"
			controller.params.inArchive = true
			controller.params.searchString = "work"
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('+254987654')]
	}
	
	def "can include archived messages in search (or not)"() {
		when:
			controller.params.searchString = "work"
			controller.params.inArchive = true
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('+254111222'), Fmessage.findBySrc('+254987654')]
			
		when:
			controller.params.searchString = "work"
			controller.params.inArchive = false
			model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('+254111222')]
	}
	
	def "deleted messages do not appear in search results"() {
		when:
			controller.params.searchString = "liver"
			controller.params.activityId = "poll-${Poll.findByTitle('Miauow Mix').id}"
			Fmessage.findBySrc("+254333222").deleted = true
			Fmessage.findBySrc("+254333222").save(flush: true)
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('Minime')]
	}

	def "messageInstanceTotal should give a total count of all the messages available"() {
		when:
			controller.params.searchString = "w"
			controller.params.inArchive = true
			controller.params.max = "1"
			controller.params.offset = "0"
			def model = controller.result()
		then:
			model.messageInstanceList.size() == 1
			model.messageInstanceTotal == 3

	}
	
	//There is a total of 9 messages but one is achived. So a maximum of 8 will be returned.
	def "if only end date is define, return message before this date"() {
		when:
			controller.params.endDate = new Date()-1
			def model = controller.result()
		then:
			model.messageInstanceTotal == 2
		when:
			controller.params.endDate = new Date()-5
			model = controller.result()
		then:
			model.messageInstanceTotal == 2
		when:
			controller.params.endDate = new Date()-6
			model = controller.result()
		then:
			model.messageInstanceTotal == 1
	}
	
	def "if only start date is define, return message after this date"() {
		when:
			controller.params.startDate = new Date()-1
			def model = controller.result()
		then:
			model.messageInstanceTotal == 6
		when:
			controller.params.startDate = new Date()-4
			model = controller.result()
		then:
			model.messageInstanceTotal == 6
		when:
			controller.params.startDate = new Date()-5
			model = controller.result()
		then:
			model.messageInstanceTotal == 7
	}
	
	def "only return message within the specific time range"() {
		when:
			controller.params.startDate = new Date()-4
			controller.params.endDate = new Date()
			def model = controller.result()
		then:
			model.messageInstanceTotal == 6
		when:
			controller.params.startDate = new Date()
			controller.params.endDate = new Date()
			model = controller.result()
		then:
			model.messageInstanceTotal == 6
		when:
			controller.params.startDate = new Date()-6
			controller.params.endDate = new Date()-3
			model = controller.result()
		then:
			model.messageInstanceTotal == 1
		when:
			controller.params.startDate = new Date()-7
			controller.params.endDate = new Date()-5
			model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('Bob'), Fmessage.findBySrc('Michael')]
		when:
			controller.params.startDate = new Date()-14
			controller.params.endDate = new Date()
			model = controller.result()
		then:
			model.messageInstanceTotal == 8
	}
	
	// TODO this needs a proper cleanup
	def "only return message with custom fields"() {
		when:
			controller.params['cityCustomField'] = 'Paris'
			//controller.params.inArchive = true
			def model = controller.result()
			//println("the fmessage.contactName is "+Fmessage.findBySrcLike("+254987654").contactName)
		then:
			model.messageInstanceList == Fmessage.findAllByContactNameLikeAndArchived('Alex', false)
			//model.messageInstanceTotal == 1
		when:
			controller.params['cityCustomField'] = ''
			controller.params['likeCustomField'] = 'ak'
		    model = controller.result()
		then:
			//println(model.messageInstanceList.toString()+" "+model.messageInstanceList.src+" => "+model.messageInstanceList.dst)
			//println("toto message: "+Fmessage.findByDst('+666666666').contactName)
			//model.messageInstanceList == Fmessage.findAllByDst('+666666666')+ Fmessage.findAllBySrc('+254333222')
			model.messageInstanceTotal == 2
		when:
			controller.params['cityCustomField'] = ''
			controller.params['likeCustomField'] = ''
			controller.params['dobCustomField'] = '7'
			model = controller.result()
		then:
			model.messageInstanceTotal == 1
		when:
			controller.params['dobCustomField'] = ''
			controller.params['cityCustomField'] = 'sometingthatdoesntexit'
			model = controller.result()
		then:
			model.messageInstanceTotal == 0
		when:
			controller.params['cityCustomField'] = ''
			model = controller.result()
		then:
			model.messageInstanceTotal == 8
	}
}
