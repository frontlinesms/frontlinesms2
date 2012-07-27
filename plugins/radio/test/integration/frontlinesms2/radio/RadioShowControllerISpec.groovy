package frontlinesms2.radio
import frontlinesms2.*
import java.util.Date
import frontlinesms2.Trash

class RadioShowControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new RadioShowController()
	}
	
	def "startShow changes 'isRunning' show to true"() {
		given:
			def show = new RadioShow(name:"Test").save(flush:true)
			assert !show.isRunning
		when:
			controller.params.id = show.id
			controller.startShow()
			show.refresh()
		then:
			show.isRunning
	}
	
	def "should not start another show when one is currently running"() {
		given:
			def show = new RadioShow(name:"Test 1").save(flush:true)
			def show2 = new RadioShow(name:"Test 2").save(flush:true)
			assert !show.isRunning
		when:
			controller.params.id = show.id
			controller.startShow()
			show.refresh()
		then:
			show.isRunning
		when:
			controller.params.id = show2.id
			controller.startShow()
			show2.refresh()
		then:
			!show2.isRunning
			controller.flash.message == "Test 1 show is already on air"
	}
	
	def "stopShow changes the 'isRunning' show to false"() {
		given:
			def show = new RadioShow(name:"Test 1").save(flush:true)
			show.start()
			assert show.isRunning
		when:
			controller.params.id = show.id
			controller.stopShow()
			show.refresh()
		then:
			!RadioShow.findByName("Test 1").isRunning
	}

	def "Adding a poll to a new radio show removes it from the previous radio show"() {
		given:
			def show1 = new RadioShow(name:"Test 1").save()
			def show2 = new RadioShow(name:"Test 2").save()
			def poll = new Poll(name:"Test Poll")
			poll.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
			poll.save(failOnError:true)
			show1.addToActivities(poll)
			show1.save()
			assert show1.activeActivities.size() == 1
		when:
			controller.params.activityId = poll.id
			controller.params.radioShowId = show2.id
			controller.addActivity()
			show1.refresh()
			show2.refresh()
		then:
			show2.activeActivities.size() == 1
			!show1.activeActivities
	}
	
	def "message can be moved to a folder"() {
		setup:
			def show1 = new RadioShow(name:"Test 1").save(flush:true)
			def message = new Fmessage(src:'Bob', date: new Date(), text:'I like manchester', inbound:true).save(failOnError: true, flush:true)
			def controller = new MessageController()
		when:
			controller.params.messageId = ',' + message.id + ','
			controller.params.ownerId = show1.id
			controller.params.messageSection = 'radioShow'
			controller.move()
		then:
			show1.getShowMessages(false).find {message}
			message.messageOwner == show1
	}
	
	def "can export messages from a radio show"() {
		given:
			def controller = new ExportController()
			def show = new RadioShow(name:"Test 1").save(flush:true)
			def message = new Fmessage(src:'Bob', date: new Date(), text:'I like manchester', inbound:true).save(failOnError: true, flush:true)
			show.addToMessages(message).save(flush:true)
			controller.params.messageSection = "radioShow"
			controller.params.ownerId = RadioShow.findByName("Test 1").id
		when:
			def result = controller.downloadMessageReport()
		then:
			result['messageInstanceList'].size() == 1
	}

	def "can delete a radio show to send it to the trash"() {
		setup:
			def show1 = new RadioShow(name:"Test 1").save(flush:true)
			assert RadioShow.findAllByDeleted(false) == [show1]
			assert !Trash.findByObject(show1)
			controller.params.id  = show1.id
		when:
			controller.delete()
		then:
			RadioShow.findAllByDeleted(true) == [show1]
			RadioShow.findAllByDeleted(false) == []
			assert Trash.findByObject(show1)
	}

	def "can restore a radio show to move out of the trash"() {
		setup:
			def show1 = new RadioShow(name:"Test 1", deleted:true).save()
			def trashedShow = new Trash(displayName:show1.name,
				displayText:"20 messages",
				objectClass:show1.class.name,
				objectId:show1.id).save()
			assert RadioShow.findAllByDeleted(true) == [show1]
			assert Trash.findByObject(show1)
			controller.params.id = show1.id
		when:
			controller.restore()
		then:
			RadioShow.findAllByDeleted(false) == [show1]
			RadioShow.findAllByDeleted(true) == []
			!Trash.findByObject(show1)
	}

	def "can delete a radio show with its associated activities to send it to the trash"() {
		setup:
			def show1 = new RadioShow(name:"Test 1").save(flush:true)
			def poll = new Poll(name: 'Who is badder?', question: "question", autoReplyText: "Thanks")
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(failOnError:true, flush:true)
			show1.addToActivities(poll)
			show1.save(failOnError:true, flush:true)
			assert RadioShow.findAllByDeleted(false) == [show1]
			assert Poll.findAllByDeleted(false) == [poll]
			assert !Trash.findByObject(show1)
			controller.params.id  = show1.id
		when:
			controller.delete()
		then:
			RadioShow.findAllByDeleted(true) == [show1]
			RadioShow.findAllByDeleted(false) == []
			Poll.findAllByDeleted(true) == [poll]
			Poll.findAllByDeleted(false) == []
			Trash.findByObject(show1)
	}

	def "can restore a radio show with its associated activities to move out of the trash"() {
		setup:
			def show1 = new RadioShow(name:"Test 1", deleted: true).save()
			def poll = new Poll(name: 'Who is badder?', question: "question", autoReplyText: "Thanks", deleted: true)
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.save()
			show1.addToActivities(poll)
			show1.save()
			controller.params.id = show1.id
			def trashedShow = new Trash(displayName:show1.name,
				displayText:"20 messages",
				objectClass:show1.class.name,
				objectId:show1.id).save()
			assert RadioShow.findAllByDeleted(true) == [show1]
			assert Trash.findByObject(show1)
		when:
			controller.restore()
		then:
			RadioShow.findAllByDeleted(false) == [show1]
			RadioShow.findAllByDeleted(true) == []
			Poll.findAllByDeleted(false) == [poll]
			Poll.findAllByDeleted(true) == []
			!Trash.findByObject(show1)
	}

	def "can archive a show, and all related activities get archived"() {
		setup:
			def show1 = new RadioShow(name:"Test 1").save(flush:true, failOnError:true)
			def poll = new Poll(name: 'Who is badder?', question: "question", autoReplyText: "Thanks")
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(flush:true, failOnError:true)
			show1.addToActivities(poll)
			show1.save(flush:true, failOnError:true)
			controller.params.id = show1.id
		when:
			controller.archive()
		then:
			show1.archived
			poll.archived
	}

	def "can unarchive a show, and all related activities get unarchived"() {
		setup:
			def show1 = new RadioShow(name:"Test 1", archived:true).save(flush:true, failOnError:true)
			def poll = new Poll(name: 'Who is badder?', question: "question", autoReplyText: "Thanks", archived:true)
			poll.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(flush:true, failOnError:true)
			show1.addToActivities(poll)
			show1.save(flush:true, failOnError:true)
			controller.params.id = show1.id
		when:
			controller.unarchive()
		then:
			!show1.archived
			!poll.archived
	}

@spock.lang.IgnoreRest
	def "show will not be unarchived if its keyword is in use by other show"() {
		setup:
			def show1 = new RadioShow(name:"Test 1", archived:true).save()
			def poll1 = new Poll(name: 'Who is badder?', question: "question", autoReplyText: "Thanks", archived:true)
			poll1.keyword = new Keyword(value: "THEKEYWORD", activity:poll1)
			poll1.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll1.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll1.addToResponses(PollResponse.createUnknown())
			poll1.save(flush:true, failOnError:true)
			show1.addToActivities(poll1)
			show1.save(flush:true, failOnError:true)

			def show2 = new RadioShow(name:"Test 2").save()
			def poll2 = new Poll(name: 'Who is baddest?', question: "question", autoReplyText: "Thanks")
			poll2.keyword = new Keyword(value: "THEKEYWORD", activity: poll2)
			poll2.addToResponses(new PollResponse(key: 'A', value: 'Michael-Jackson'))
			poll2.addToResponses(new PollResponse(key: 'B', value: 'Chuck-Norris'))
			poll2.addToResponses(PollResponse.createUnknown())
			poll2.save(flush:true, failOnError:true)
			show2.addToActivities(poll2)
			show2.save(flush:true, failOnError:true)

			controller.params.id = show1.id
		when:
			controller.unarchive()
		then:
			poll1.archived
			show1.archived
	}
}