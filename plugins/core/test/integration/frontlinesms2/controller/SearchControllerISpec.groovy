package frontlinesms2.controller

import frontlinesms2.*
import spock.lang.*

class SearchControllerISpec extends grails.plugin.spock.IntegrationSpec {
	final Date TEST_DATE = new Date()
	
	def controller
	def firstContact, secondContact, thirdContact
	def group
	def folder


	def setup() {
		controller = new SearchController()
		firstContact = new Contact(name:'Alex', mobile:'+254987654').save(failOnError:true, flush:true)
		secondContact = new Contact(name:'Mark', mobile:'+254333222').save(failOnError:true, flush:true)
		thirdContact = new Contact(name:"Toto", mobile:'+666666666').save(failOnError:true, flush:true)
		group = new Group(name:'test').save(failOnError:true)
		new Group(name:'nobody').save(failOnError:true, flush:true )
		
		//message in the same day will still be return even if in the future
		def futureDate = new Date()
		futureDate.hours = futureDate.hours + 1
		
		Fmessage.build(src:'+254987654', text:'work at 11.00', archived:true, date:TEST_DATE)
				.save(failOnError:true, flush:true)
		Fmessage.build(src:'+254987654', text:'finaly i stay in bed', date:TEST_DATE)
				.save(failOnError:true, flush:true)
		Fmessage.build(src:'+254111222', text:'work is awesome', date:futureDate)
				.save(failOnError:true, flush:true)
		Fmessage.build(src:'Bob', text:'hi Bob', date:TEST_DATE-5)
				.save(failOnError:true, flush:true)
		Fmessage.build(src:'Michael', text:'Can we get meet in 5 minutes', date:TEST_DATE-7)
				.save(failOnError:true, flush:true)
		Fmessage.build(src:'+666666666', text:'finally i stay in bed', date:TEST_DATE)
				.save(failOnError:true, flush:true)
				
		[new CustomField(name:'city', value:'Paris', contact: firstContact),
				new CustomField(name:'like', value:'cake', contact: secondContact),
				new CustomField(name:'ik', value:'car', contact: secondContact),
				new CustomField(name:'like', value:'ake', contact: thirdContact),
				new CustomField(name:'dob', value:'12/06/79', contact: secondContact)].each {
			it.save(failOnError:true, flush:true)
		}

		def chickenMessage = Fmessage.build(src:'Barnabus', text:'i like chicken', date:TEST_DATE)
				.save(failOnError:true, flush:true)
		def liverMessage = Fmessage.build(src:'Minime', text:'i like liver', date:TEST_DATE)
				.save(failOnError:true, flush:true)
		def liverMessage2 = Fmessage.build(src:'+254333222', text:'liver for lunch?', date:TEST_DATE)
				.save(failOnError:true, flush:true)
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		def unknownResponse = new PollResponse(value:'unknown')
		def poll = new Poll(name:'Miauow Mix')
				.addToResponses(unknownResponse)
				.addToResponses(chickenResponse)
				.addToResponses(liverResponse)
				.save(failOnError:true, flush:true)
		liverResponse.addToMessages(liverMessage)
		liverResponse.addToMessages(liverMessage2)
		chickenResponse.addToMessages(chickenMessage)
		poll.save(failOnError:true, flush:true)
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
			model.messageInstanceList*.src == ['Barnabus']
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
			model.messageInstanceList*.src == ['+254111222']
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

	def "search for outgoing messages only"() {
		setup:
			3.times { new Fmessage(src:'src', date:TEST_DATE)
					.addToDispatches(dst:'123456', status:DispatchStatus.PENDING)
					.save(flush:true, failOnError:true) }
		when:
			controller.params.messageStatus = 'SENT, PENDING, FAILED'
			def model = controller.result()
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
			model.messageInstanceList*.src == ['+254333222']
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
			model.messageInstanceList*.src == ['+254333222']
	}
	
	def "message searches can be restricted to individual contacts"() {
		when:
			controller.params.contactString = "alex"
			controller.params.inArchive = true
			controller.params.searchString = "work"
			def model = controller.result()
		then:
			model.messageInstanceList*.src == ['+254987654']
	}
	
	def "can include archived messages in search (or not)"() {
		when:
			controller.params.searchString = "work"
			controller.params.inArchive = true
			def model = controller.result()
		then:
			model.messageInstanceList*.src == ['+254111222', '+254987654']
			
		when:
			controller.params.searchString = "work"
			controller.params.inArchive = false
			model = controller.result()
		then:
			model.messageInstanceList*.src == ['+254111222']
	}
	
	def "deleted messages do not appear in search results"() {
		when:
			controller.params.searchString = "liver"
			controller.params.activityId = "activity-${Poll.findByName('Miauow Mix').id}"
			Fmessage.findBySrc("+254333222").isDeleted = true
			Fmessage.findBySrc("+254333222").save(flush: true)
			def model = controller.result()
		then:
			model.messageInstanceList*.src == ['Minime']
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
	
	@Unroll
	def "only return message within the specific time range"() {
		when:
			controller.params.startDate = TEST_DATE - startDelta
			controller.params.endDate = TEST_DATE - endDelta
			controller.params.inArchive = archived
			def model = controller.result()
		then:
			model.messageInstanceTotal == messageTotal
		where:
			archived | startDelta | endDelta | messageTotal
			null     | 4          | 0        | 5
			null     | 0          | 0        | 5
			null     | 6          | 3        | 1
			null     | 7          | 5        | 2+0
			true     | 14         | 0        | 8
	}
	
	@Unroll
	def "only return message with custom fields"() {
		when:
			params.each { k, v -> controller.params[k] = v }
			def model = controller.result()
		then:
			model.messageInstanceTotal == messageTotal
		where:
			messageTotal | params
			2            | [city:'Paris', inArchive:true]
			2            | [like:'ak', inArchive:true]
			1            | [dob:'7', inArchive:true]
			0            | [city:'somethingthatdoesntexist', inArchive:true]
			9            | [inArchive:true]
	}
}
