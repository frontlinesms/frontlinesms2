package frontlinesms2

import spock.lang.*

import org.apache.camel.*

@TestFor(WebconnectionActionStep)
@Mock(StepProperty)
class WebconnectionActionStepSpec extends Specification {
	def webconnectionService

	def setup() {
		webconnectionService = Mock(WebconnectionService)
		webconnectionService.getProcessedValue(_,_) >> {it, fmessage -> it.value}
		Fmessage.metaClass.static.get = { Serializable id ->
			return Mock(Fmessage)
		}
	}

	def "activating the step should call the webconnectionService.activate"() {
		given:
			def step = new WebconnectionActionStep()
			step.webconnectionService = webconnectionService
		when:
			step.activate()
		then:
			1 * webconnectionService.activate(step)
	}

	def "deactivating the step should call the webconnectionService.deactivate"() {
		given:
			def step = new WebconnectionActionStep()
			step.webconnectionService = webconnectionService
		when:
			step.deactivate()
		then:
			1 * webconnectionService.deactivate(step)
	}

	def "preProcess should encode all the step properties associated with the webconnectionActionStep"() {
		given:
			def step = new WebconnectionActionStep()
			step.webconnectionService = webconnectionService
			["url":"http://test.me", "httpMethod":"GET", "param:message":'${messageText}'].each { k,v ->
				println "creating stepProp with k:$k, v:$v"
				def stepProp = new StepProperty(key:k, value:v)
				step.addToStepProperties(stepProp)
			}
			println "step.props >> ${step.stepProperties}"
			step.save(flush:true)
			def mockExchange = mockExchange('', [:])
		when:
			step.preProcess(mockExchange)
		then:
			mockExchange.in.headers*.value.containsAll(["http://test.me", "GET", "message=%24%7BmessageText%7D"])
			!mockExchange.in.body
	}

	Exchange mockExchange(body, Map headers) {
		Exchange x = Mock()
		def inMessage = Mock(Message)
		if(body) inMessage.body >> body
		inMessage.headers >> headers
		x.in >> inMessage
		return x
	}
}