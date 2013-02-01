package frontlinesms2.domain

import spock.lang.*
import frontlinesms2.*
import grails.converters.JSON

class UshahidiWebconnectionISpec extends grails.plugin.spock.IntegrationSpec {
	def webCService = Mock(WebconnectionService)

	def 'incoming message matching keyword should trigger http message sending'() {
		given:
			def k = new Keyword(value:'FORWARD')
			def webconnection = new UshahidiWebconnection(name:"CrowdahidiSMS", url:"http://www.ushahidi.com/frontlinesms/",httpMethod:Webconnection.HttpMethod.GET).addToKeywords(k).save(failOnError:true)
			webconnection.webconnectionService = webCService
			webconnection.save(failOnError:true)
			def incomingMessage = Fmessage.build(text:"FORWARD ME", src:'123123')
		when:
			webconnection.processKeyword(incomingMessage, k)
		then:
			1 * webCService.send(incomingMessage)
	}

	def 'incoming message should match if keyword is blank and exactmatch == false'() {
		given:
			def k = new Keyword(value:'')
			def webconnection = new UshahidiWebconnection(name:"CrowdahidiSMS", url:"http://www.ushahidi.com/frontlinesms/",httpMethod:Webconnection.HttpMethod.GET).addToKeywords(k).save(failOnError:true)
			webconnection.webconnectionService = webCService
			webconnection.save(failOnError:true)
			def incomingMessage = Fmessage.build(text:"FORWARD ME", src:'123123')
			webconnection.addToMessages(incomingMessage).save(failOnError:true)
		when:
			webconnection.processKeyword(incomingMessage, k)
		then:
			1 * webCService.send(incomingMessage)
	}
}
