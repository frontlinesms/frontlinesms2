package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class MessageControllerSpec extends ControllerSpec {

	def setup() {
		mockDomain Fmessage
		mockParams.messageText = "text"
		controller.messageSendService = new MessageSendService()
	}

	def "should send a message to the given address"() {
		setup:
			mockParams.addresses = "+919544426000"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.count() == 1
	}

	def "should send message to each recipient in the list of address"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			mockParams.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.list()*.dst == addresses
			Fmessage.count() == 3
	}
}