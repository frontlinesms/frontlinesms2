package frontlinesms2.radio

import frontlinesms2.Fmessage
import grails.plugin.spock.UnitSpec

class RadioShowServiceSpec extends UnitSpec {
	RadioShowService radioService
	
	def setup() {
		radioService = new RadioShowService()
	}
	
	def "radioShowService can only initialize one radio show at any given time"() {
		setup:
			mockDomain(RadioShow)
			def show = new RadioShow(name:"Health Show").save()
			def show2 = new RadioShow(name:"Agriculture Show").save()
		when:
			radioService.startShow(show)
		then:
			radioService.isRunning()
			radioService.currentShow == show
		when:
			radioService.startShow(show2)
		then:
			radioService.isRunning()
			radioService.currentShow == show
	}
	
	def "radioShowService can only initialize a different show when the current one is stopped"() {
		setup:
			mockDomain(RadioShow)
			def show = new RadioShow(name:"Health Show").save()
			def show2 = new RadioShow(name:"Agriculture Show").save()
		when:
			radioService.startShow(show)
		then:
			radioService.currentShow == show
		when:
			radioService.stopShow()
			radioService.startShow(show2)
		then:
			radioService.isRunning()
			radioService.currentShow == show2
	}
	
}