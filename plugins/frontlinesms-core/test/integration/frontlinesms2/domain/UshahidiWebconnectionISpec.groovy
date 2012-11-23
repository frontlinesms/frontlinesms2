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

	def 'can edit an existing ushahidi web connection'(){
		given: 'an UshahidiWebconnection exists'
			def keyword = new Keyword(value:'USHAHIDI')
			def webConnectionInstance = new UshahidiWebconnection(name:"Trial", url:"https://trial.crowdmap.com", httpMethod:Webconnection.HttpMethod.POST).addToKeywords(keyword).save(failOnError:true)			
		and:
			def controller = new WebconnectionController()
		when: 'new parameters are passed'
			controller.params.ownerId = webConnectionInstance.id
			controller.params.webconnectionType = 'ushahidi'
			controller.params.name = 'Trial'
			controller.params.url = 'https://frontlineCrowd.crowdmap.com'
			controller.params.key = '2343asdasd'
			controller.params.keyword = 'Repo'
		and: 'save action is called'
			controller.save()
		then: 'existing UshahidiWebconnection properties should have changed'
			def connection = UshahidiWebconnection.findByName('Trial')
			connection.name == "Trial"
			connection.url == "https://frontlineCrowd.crowdmap.com"
			connection.requestParameters*.value.containsAll(["2343asdasd"])
	}
}
