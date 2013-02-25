package frontlinesms2.controller

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import frontlinesms2.*
import grails.converters.JSON

class WebconnectionControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def trashService
	def i18nUtilService
	def withUshahidiWebconnection = { c -> c.call(UshahidiWebconnection.get(controller.params.ownerId) ?: UshahidiWebconnection.newInstance()) }
	def withGenericWebconnection = { c -> c.call(GenericWebconnection.get(controller.params.ownerId) ?: GenericWebconnection.newInstance()) }

//TODO Asserts need refractoring
	def setup() {
		controller = new WebconnectionController()
	}

	def 'save action should also save an Ushahidi webconnection'() {
		setup:
			controller.params.name = "Test Webconnection"
			controller.params.httpMethod = "get"
			controller.params.url = "http://www.ushahidi.com"
			controller.params.keywords = "keyword"
			controller.params.webconnectionType = "ushahidi"
			controller.params.key = '12345678'
			controller.withWebconnection = withUshahidiWebconnection
		when:
			controller.save()
		then:
			Webconnection.findByName("Test Webconnection").name == controller.params.name
			controller.flash.message == i18nUtilService.getMessage([code:"webconnection.save.success", args:[Webconnection.findByName("Test Webconnection").name]])
			Webconnection.findByName("Test Webconnection").url == "http://www.ushahidi.com"
			RequestParameter.findByName('key').value == '12345678'
			RequestParameter.findByName('m').value == '${message_body}'
			RequestParameter.findByName('s').value == '${message_src_number}'
	}

	def 'save action should also save a generic webconnection'() {
		setup:
			controller.params.name = "Test Webconnection"
			controller.params.httpMethod = "get"
			controller.params.url = "http://www.frontlinesms.com/sync"
			controller.params.keywords = "keyword"
			controller.params.webconnectionType = "generic"
			controller.params.'param-name' = 'username'
			controller.params.'param-value' = 'bob'
			controller.withWebconnection = withGenericWebconnection
		when:
			controller.save()
		then:
			Webconnection.findByName("Test Webconnection").name == controller.params.name
			RequestParameter.findByName('username').value == 'bob'
	}
	
	def 'edit should save all the details from the walkthrough'() {
		setup:
			def keyword = new Keyword(value:'TEST')
			def webconnection = new GenericWebconnection(name:"Old Webconnection name", url:"http://www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.POST)
			webconnection.addToKeywords(keyword).save(failOnError:true)

			controller.params.ownerId = webconnection.id
			controller.params.name = "New Webconnection name"
			controller.params.url = "http://www.frontlinesms.com/syncing"
			controller.params.httpMethod = "get"
			controller.params.keywords = "keyword"
			controller.params.sorting = "enabled"
			controller.params.webconnectionType = "generic"
			controller.params.'param-name' = ['username', 'password'] as String[]
			controller.params.'param-value' = ['bob','secret'] as String[]
			controller.withWebconnection = withGenericWebconnection
		when:
			controller.save()
			webconnection.refresh()
		then:
			webconnection.name == "New Webconnection name"
			webconnection.keywords*.value == ["KEYWORD"]
			webconnection.httpMethod == Webconnection.HttpMethod.GET
			webconnection.url == "http://www.frontlinesms.com/syncing"
			webconnection.requestParameters.size() == 2
			webconnection.requestParameters*.name.containsAll(['username','password'])
			webconnection.requestParameters*.value.containsAll(['bob','secret'])
	}

	def 'edit should remove requestParameters from a web connection'() {
		setup:
			def keyword = new Keyword(value:'COOL')
			def webconnection = new GenericWebconnection(name:"Test", url:"http://www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.POST).addToKeywords(keyword)
			webconnection.addToRequestParameters(new RequestParameter(name:"name", value:'${name}'))
			webconnection.addToRequestParameters(new RequestParameter(name:"age", value:'${age}'))
			webconnection.save(failOnError:true)
			controller.params.ownerId = webconnection.id
			controller.params.name = "Test Connection"
			controller.params.httpMethod = "post"
			controller.params.keywords = "Test"
			controller.params.webconnectionType = "generic"
			controller.params.'param-name' = "username"
			controller.params.'param-value' = "geoffrey"
			controller.withWebconnection = withGenericWebconnection
		when:
			controller.save()
		then:
			webconnection.name == "Test Connection"
			webconnection.httpMethod == Webconnection.HttpMethod.POST
			webconnection.requestParameters.size() == 1
			webconnection.requestParameters*.name == ['username']
	}

	def "should not save requestParameters without a name value"() {
		setup:
			def keyword = new Keyword(value:'AWESOME')
			def webconnection = new GenericWebconnection(name:"Generic", url:"http://www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.POST).addToKeywords(keyword)
			webconnection.addToRequestParameters(new RequestParameter(name:"name", value:'${name}'))
			webconnection.addToRequestParameters(new RequestParameter(name:"age", value:'${age}'))
			webconnection.save(failOnError:true)
			controller.params.ownerId = webconnection.id
			controller.params.name = "Generic Connection"
			controller.params.keywords = "Test"
			controller.params.httpMethod = "post"
			controller.params.'param-name' = ""
			controller.params.webconnectionType = "generic"
			controller.params.'param-value' = "geoffrey"
			controller.withWebconnection = withGenericWebconnection
		when:
			controller.save()
		then:
			webconnection.name == "Generic Connection"
			!webconnection.requestParameters
	}

	def "editing a webconnection should persist changes"(){
		setup:
			def keyword = new Keyword(value:"TRIAL")
			def connection = new UshahidiWebconnection(name:"Trial", url:"http://www.ushahidi.com/frontlinesms2", httpMethod:Webconnection.HttpMethod.POST).addToKeywords(keyword)
			connection.save(failOnError:true)
			controller.params.ownerId = connection.id
			controller.params.name = "Ushahidi Connection"
			controller.params.url = "http://sane.com"
			controller.params.keywords = "Test"
			controller.params.webconnectionType = "ushahidi"
			controller.params.httpMethod = "get"
			controller.params.key = "get"
			controller.withWebconnection = withUshahidiWebconnection
		when:
			controller.save()
		then:
			connection.name == "Ushahidi Connection"
			connection.name != "Trial"
			connection.url == "http://sane.com"
			connection.url != "http://www.ushahidi.com/frontlinesms2"
			connection.httpMethod ==  Webconnection.HttpMethod.GET
			connection.httpMethod !=  Webconnection.HttpMethod.POST
	}

	def "can update keywords"(){
		setup:
			def connection = new UshahidiWebconnection(name:"Trial", url:"http://www.ushahidi.com/frontlinesms2", httpMethod:Webconnection.HttpMethod.POST)
			(1..4).each { connection.addToKeywords(new Keyword(value:"KEYWORD${it}")) }
			connection.save(failOnError:true)
			controller.params.ownerId = connection.id
			controller.params.name = "Ushahidi Connection"
			controller.params.url = "http://sane.com"
			controller.params.keywords = "KEYWORD3,KEYWORD4,KEYWORD5"
			controller.params.webconnectionType = "ushahidi"
			controller.params.httpMethod = "get"
			controller.params.key = "get"
			controller.withWebconnection = withUshahidiWebconnection
		when:
			controller.save()
		then:
			connection.name == "Ushahidi Connection"
			connection.name != "Trial"
			connection.url == "http://sane.com"
			connection.url != "http://www.ushahidi.com/frontlinesms2"
			connection.httpMethod ==  Webconnection.HttpMethod.GET
			connection.httpMethod !=  Webconnection.HttpMethod.POST
	}

	@Unroll
	def 'while editing a webconnection changing the sorting criteria should translate into proper keyword changes'(){
		setup:
			controller.params.name = "Ushahidi Connection"
			controller.params.url = "http://sane.com"
			controller.params.keywords = "Test,testing"
			controller.params.webconnectionType = "ushahidi"
			controller.params.httpMethod = "get"
			controller.params.key = "get"
			controller.withWebconnection = withUshahidiWebconnection
		when:
			controller.params.sorting = sorting
			controller.save()
		then:
			println ">>>>>>>>>>>>> ${UshahidiWebconnection.findByName("Ushahidi Connection").keywords?.value}"
			results == UshahidiWebconnection.findByName("Ushahidi Connection").keywords*.value?.join(',')
		where:
			sorting|results
			"global"|''
			"enabled"|"TEST,TESTING"
			"disabled"|null
	}

	def 'retry failed action should result in all failed uploads being reattempted'() {
		given:
			def keyword = new Keyword(value:'TEST')
			def webconnection = new GenericWebconnection(name:"Webconnection with failures", url:"http://www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.POST)
			webconnection.addToKeywords(keyword)
			webconnection.save(failOnError:true, flush:true)

			// adding 3 successful uploads, two failed ones, and one pending
			def m
			5.times { it ->
				m = new Fmessage(text:"test", inbound:true, src:"+1234$it").save(failOnError:true)
				m.setMessageDetailValue(webconnection, ((it % 2) ? Webconnection.OWNERDETAIL_FAILED : Webconnection.OWNERDETAIL_SUCCESS ))
				webconnection.addToMessages(m)
			}

			m = new Fmessage(text:"test", inbound:true,src:"+12345").save(failOnError:true)
			m.setMessageDetailValue(webconnection, Webconnection.OWNERDETAIL_PENDING)
			webconnection.addToMessages(m)

			webconnection.save(failOnError:true, flush:true)
			controller.params.ownerId = webconnection.id
		when:
			controller.retryFailed()
		then: "Successful webconnections should not be changed"
			Fmessage.findAllBySrcInList(["+12340", "+12342", "+12344"])*.ownerDetail == [Webconnection.OWNERDETAIL_SUCCESS] * 3
		and: "Failed webconnections should be retried"
			Fmessage.findAllBySrcInList(["+12341", "+12343", "+12345"])*.ownerDetail == [Webconnection.OWNERDETAIL_PENDING] * 3
	}

	def 'can edit an existing ushahidi web connection'(){
		given:
			def keyword = new Keyword(value:'USHAHIDI')
			def webConnectionInstance = new UshahidiWebconnection(name:"Trial", url:"https://trial.crowdmap.com", httpMethod:Webconnection.HttpMethod.POST).addToKeywords(keyword).save(failOnError:true)			
			controller.params.ownerId = webConnectionInstance.id
			controller.params.webconnectionType = 'ushahidi'
			controller.params.name = 'Trial'
			controller.params.url = 'https://frontlineCrowd.crowdmap.com'
			controller.params.key = '2343asdasd'
			controller.params.keyword = 'Repo'
			controller.withWebconnection = withUshahidiWebconnection
		when:
			controller.save()
		then:
			def connection = UshahidiWebconnection.findByName('Trial')
			connection.name == "Trial"
			connection.url == "https://frontlineCrowd.crowdmap.com"
			connection.requestParameters*.value.containsAll(["2343asdasd"])
	}
}

