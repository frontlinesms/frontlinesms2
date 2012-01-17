package frontlinesms2.radio
import frontlinesms2.*

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
	
	def "list of polls belonging to radio shows are not included in the pollInstanceList"() {
		setup:
			def show = new RadioShow(name:"Health & fitness")
			def poll = Poll.createPoll(title: 'Who is badder?', choiceA:'Michael-Jackson', choiceB:'Chuck-Norris', question: "question").save(failOnError:true, flush:true)
			def poll2 = Poll.createPoll(title: 'Who will win?', choiceA:'Uhuru Kenyatta', choiceB:'Fred Ruto', question: "politics").save(failOnError:true, flush:true)
			show.addToPolls(poll)
			show.addToPolls(poll2)
			show.save(flush:true)
			[Poll.createPoll(title: 'Health Poll', choiceA:'Healthy', choiceB:'Unhealthy', question: "question"),
				Poll.createPoll(title: 'Water Poll', choiceA:'hard water', choiceB:'soft water', question: "question")].each {
				it.save(failOnError:true, flush:true)
				}
			println "no. of polls are ${Poll.findAll()}"
		when:
			def model = controller.getShowModel(null)
		then:
			model.pollInstanceList.size() == 2
	}
}