package frontlinesms2.radio

import frontlinesms2.Fmessage

class RadioShowServiceISpec extends grails.plugin.spock.IntegrationSpec {
	
	RadioShowService radioService
	
	def setup() {
		radioService = new RadioShowService()
	}
	
	def "incoming messages are saved to the current running show"() {
		setup:
			def show = new RadioShow(name:"Morning Show", isRunning:true).save(failOnError:true, flush:true)
			def message = new Fmessage(src:"123456").save(failOnError:true, flush:true)
		when:
			radioService.process(message)
			show.save(flush:true)
		then:
			Fmessage.findAll()[0].messageOwner == show
	}

}