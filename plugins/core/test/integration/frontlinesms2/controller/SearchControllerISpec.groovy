package frontlinesms2.controller

import java.text.SimpleDateFormat
import frontlinesms2.*

class SearchControllerISpec extends grails.plugin.spock.IntegrationSpec {
	final Date TEST_DATE = new Date()
	
	def controller
	def firstContact, secondContact, thirdContact
	def group
	def folder


	def setup() {
		controller = new SearchController()
		firstContact = new Contact(name:'Alex', mobile:'+254987654').save(failOnError:true)
		secondContact = new Contact(name:'Mark', mobile:'+254333222').save(failOnError:true)
		thirdContact = new Contact(name:"Toto", mobile:'+666666666').save(failOnError:true)
		group = new Group(name:'test').save(failOnError:true)
		new Group(name:'nobody').save(failOnError:true, flush:true )
		
		//message in the same day will still be return even if in the future
		def futureDate = new Date()
		futureDate.hours = futureDate.hours + 1
		
		[new Fmessage(src:'+254987654', text:'work at 11.00', archived: true, date: TEST_DATE),
				new Fmessage(src:'+254987654', text:'finaly i stay in bed', date: TEST_DATE),
				new Fmessage(src:'+254111222', date: futureDate, text:'work is awesome'),
				new Fmessage(src:'Bob', date: TEST_DATE-5, text:'hi Bob'),
				new Fmessage(src:'Michael', date: TEST_DATE-7, text:'Can we get meet in 5 minutes'),
				new Fmessage(src:'+666666666', text:'finally i stay in bed', date: TEST_DATE)].each() {
			it.inbound = true
			it.save(failOnError:true)
		}
				
		[new CustomField(name:'city', value:'Paris', contact: firstContact),
				new CustomField(name:'like', value:'cake', contact: secondContact),
				new CustomField(name:'ik', value:'car', contact: secondContact),
				new CustomField(name:'like', value:'ake', contact: thirdContact),
				new CustomField(name:'dob', value:'12/06/79', contact: secondContact)].each {
			it.save(failOnError:true)
		}

		def chickenMessage = new Fmessage(src:'Barnabus', text:'i like chicken', inbound:true, date: TEST_DATE).save(failOnError:true)
		def liverMessage = new Fmessage(src:'Minime', text:'i like liver', inbound:true, date: TEST_DATE).save(failOnError:true)
		def liverMessage2 = new Fmessage(src:'+254333222', text:'liver for lunch?', inbound:true, date: TEST_DATE).save(failOnError:true)
		def poll = new Poll(name:'Miauow Mix')
		def chickenResponse = new PollResponse(value:'chicken', poll:poll)
		def liverResponse = new PollResponse(value:'liver', poll:poll)
		def unknownResponse = new PollResponse(value:'unknown', poll:poll)
		liverResponse.addToMessages(liverMessage)
		liverResponse.addToMessages(liverMessage2)
		chickenResponse.addToMessages(chickenMessage)
		poll.addToResponses(unknownResponse)
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
			controller.params.activityId = "activity-${Poll.findByName('Miauow Mix').id}"
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('Barnabus')]
	}

// FIXME	
	def "message searches can be restricted to a folder"() {
		given:
			folder = new Folder(name: 'work').save(failOnError:true, flush:true)
			def m = Fmessage.findBySrc('+254111222')
			folder.addToMessages(m).save(failOnError: true, flush:true)
			m.save(flush:true, failOnError:true)
		when:
			controller.params.searchString = "work"
			controller.params.activityId = "folder-${folder.id}"
			def model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('+254111222')]
	}
	
	def "search for inbound messages only"() {
		when:
			controller.params.messageStatus = "INBOUND"
			controller.params.inArchive = true
			def model = controller.result()
		then:
			model.messageInstanceTotal == 9
			model.messageInstanceList.every { it.inbound }
	}

	def "search for sent messages only"() {
		setup:
			def d1 = new Dispatch(dst:'123456', status: DispatchStatus.PENDING)
			def d2 = new Dispatch(dst:'123456', status: DispatchStatus.PENDING)
			def d3 = new Dispatch(dst:'123456', status: DispatchStatus.PENDING)
			def m1 = new Fmessage(src:"src", hasPending:true, date: TEST_DATE)
			def m2 = new Fmessage(src:"src", hasSent:true, date: TEST_DATE)
			def m3 = new Fmessage(src:"src", hasFailed:true, date: TEST_DATE)
			m1.addToDispatches(d1).save(flush: true, failOnError: true)
			m2.addToDispatches(d2).save(flush: true, failOnError: true)
			m3.addToDispatches(d3).save(flush: true, failOnError: true)
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
			controller.params.activityId = "activity-${Poll.findByName('Miauow Mix').id}"
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
			controller.params.activityId = "activity-${Poll.findByName('Miauow Mix').id}"
			Fmessage.findBySrc("+254333222").isDeleted = true
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
	
	def "if only end date is defined, return message before or on this date"() {
		when:
			def model = controller.result()
		then:
			model.messageInstanceTotal == 8
		when:
			controller.params.endDate = TEST_DATE-1
			model = controller.result()
		then:
			model.messageInstanceTotal == 2
		when:
			controller.params.endDate = TEST_DATE-5
			model = controller.result()
		then:
			model.messageInstanceTotal == 2
		when:
			controller.params.endDate = TEST_DATE-6
			model = controller.result()
		then:
			model.messageInstanceTotal == 1
	}
	
	def "if only start date is defined, return only messages on or after this date"() {
		when:
			controller.params.startDate = TEST_DATE-1
			def model = controller.result()
		then:
			model.messageInstanceTotal == 6
		when:
			controller.params.startDate = TEST_DATE-4
			model = controller.result()
		then:
			model.messageInstanceTotal == 6
		when:
			controller.params.startDate = TEST_DATE-5
			model = controller.result()
		then:
			model.messageInstanceTotal == 7
	}
	
	def "only return message within the specific time range"() {
		when:
			controller.params.startDate = TEST_DATE-4
			controller.params.endDate = TEST_DATE
			def model = controller.result()
		then:
			println "list dates: ${model.messageInstanceList.date}"
			println "list messages text: ${model.messageInstanceList.text}"
			println "test date: ${TEST_DATE-5}"
			model.messageInstanceTotal == 5  // does not include archived message
		when:
			controller.params.startDate = TEST_DATE
			controller.params.endDate = TEST_DATE
			model = controller.result()
		then:
			model.messageInstanceTotal == 5
		when:
			controller.params.startDate = TEST_DATE-6
			controller.params.endDate = TEST_DATE-3
			model = controller.result()
		then:
			model.messageInstanceTotal == 1
		when:
			controller.params.startDate = TEST_DATE-7
			controller.params.endDate = TEST_DATE-5
			model = controller.result()
		then:
			model.messageInstanceList == [Fmessage.findBySrc('Bob'), Fmessage.findBySrc('Michael')]
		when:
			controller.params.startDate = TEST_DATE-14
			controller.params.endDate = TEST_DATE
			controller.params.inArchive = true
			model = controller.result()
		then:
			model.messageInstanceTotal == 8
	}
	
	// TODO this needs a proper cleanup
	def "only return message with custom fields"() {
		when:
			controller.params.city = 'Paris'
			controller.params.inArchive = true
			def model = controller.result()
		then:
			model.messageInstanceList == Fmessage.findAllByDisplayNameLike('Alex')
			model.messageInstanceTotal == 2
		when:
			controller.params.city = ''
			controller.params.like = 'ak'
		    model = controller.result()
		then:
			//println(model.messageInstanceList.toString()+" "+model.messageInstanceList.src+" => "+model.messageInstanceList.dst)
			//println("toto message: "+Fmessage.findByDst('+666666666').contactName)
			//model.messageInstanceList == Fmessage.findAllByDst('+666666666')+ Fmessage.findAllBySrc('+254333222')
			model.messageInstanceTotal == 2
		when:
			controller.params.city = ''
			controller.params.like = ''
			controller.params.dob = '7'
			model = controller.result()
		then:
			model.messageInstanceTotal == 1
		when:
			controller.params.dob = ''
			controller.params.city = 'sometingthatdoesntexit'
			model = controller.result()
		then:
			model.messageInstanceTotal == 0
		when:
			controller.params.city = ''
			model = controller.result()
		then:
			model.messageInstanceTotal == 9
	}
}
