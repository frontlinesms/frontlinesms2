package frontlinesms2.radio
import frontlinesms2.*
import java.util.Date

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
}