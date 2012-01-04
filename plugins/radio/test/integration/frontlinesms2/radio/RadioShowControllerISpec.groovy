package frontlinesms2.radio

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
			!show.isRunning
	}
}