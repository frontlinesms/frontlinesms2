package frontlinesms2.radio

import frontlinesms2.Fmessage
import frontlinesms2.MessageStorageService
import grails.plugin.spock.UnitSpec
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.Exchange
import org.apache.camel.CamelContext

class RadioShowServiceISpec extends UnitSpec {
	
	def camelContext
	RadioShowService radioService
	
	def setup() {
		messageService = new MessageStorageService()
		radioService = new RadioShowService()
	}
	
	def "incoming messages are saved to the current running show"() {
		setup:
			def show = new RadioShow(name:"Morning Show").save(failOnError:true)
			def message = new Fmessage(src:"123456").save(failOnError:true, flush:true)
		when:
			show.start()
			radioService.process(message)
			message.refresh()
			show.refresh()
		then:
			show.showMessages.list() == [message]
			message.messageOwner == show
	}

}