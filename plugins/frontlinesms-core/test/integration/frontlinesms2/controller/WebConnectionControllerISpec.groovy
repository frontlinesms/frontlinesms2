package frontlinesms2.controller

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import frontlinesms2.*
import grails.converters.JSON

class WebConnectionControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def trashService
//TODO Asserts need refractoring
	def setup() {
		controller = new WebConnectionController()
	}

	def 'save action should also save an Ushahidi webconnection'() {
		setup:
			controller.params.name = "Test WebConnection"
			controller.params.httpMethod = "get"
			controller.params.url = "www.ushahidi.com"
			controller.params.keyword = "keyword"
			controller.params.type = "ushahidi"
			controller.params.key = '12345678'
		when:
			controller.save()
		then:
			WebConnection.findByName("Test WebConnection").name == controller.params.name
			WebConnection.findByName("Test WebConnection").url == "www.ushahidi.com/frontlinesms"
			RequestParameter.findByName('key').value == '12345678'
			RequestParameter.findByName('m').value == '${message_body}'
			RequestParameter.findByName('s').value == '${message_src_name}'
	}

	def 'save action should also save a generic webconnection'() {
		setup:
			controller.params.name = "Test WebConnection"
			controller.params.httpMethod = "get"
			controller.params.url = "www.frontlinesms.com/sync"
			controller.params.keyword = "keyword"
			controller.params.type = "generic"
			controller.params.'param-name' = 'username'
			controller.params.'param-value' = 'bob'
		when:
			controller.save()
		then:
			WebConnection.findByName("Test WebConnection").name == controller.params.name
			RequestParameter.findByName('username').value == 'bob'
	}

	def 'edit should save all the details from the walkthrough'() {
		setup:
			def keyword = new Keyword(value:'TEST')
			def webConnection = new GenericWebConnection(name:"Old WebConnection name", keyword:keyword, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.POST)
			webConnection.save(failOnError:true)

			controller.params.ownerId = webConnection.id
			controller.params.name = "New WebConnection name"
			controller.params.url = "www.frontlinesms.com/syncing"
			controller.params.httpMethod = "get"
			controller.params.keyword = "keyword"
			controller.params.type = "generic"
			controller.params.'param-name' = ['username', 'password'] as String[]
			controller.params.'param-value' = ['bob','secret'] as String[]
		when:
			controller.save()
			webConnection.refresh()
		then:
			webConnection.name == "New WebConnection name"
			webConnection.keyword.value == "KEYWORD"
			webConnection.httpMethod == WebConnection.HttpMethod.GET
			webConnection.url == "www.frontlinesms.com/syncing"
			webConnection.requestParameters.size() == 2
			webConnection.requestParameters*.name.containsAll(['username','password'])
			webConnection.requestParameters*.value.containsAll(['bob','secret'])
	}

	def 'edit should remove requestParameters from a web connection'() {
		setup:
			def keyword = new Keyword(value:'COOL')
			def webConnection = new GenericWebConnection(name:"Test", keyword:keyword, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.POST)
			webConnection.addToRequestParameters(new RequestParameter(name:"name", value:'${name}'))
			webConnection.addToRequestParameters(new RequestParameter(name:"age", value:'${age}'))
			webConnection.save(failOnError:true)
			controller.params.ownerId = webConnection.id
			controller.params.name = "Test Connection"
			controller.params.httpMethod = "post"
			controller.params.keyword = "Test"
			controller.params.type = "generic"
			controller.params.'param-name' = "username"
			controller.params.'param-value' = "geoffrey"
		when:
			controller.save()
		then:
			webConnection.name == "Test Connection"
			webConnection.httpMethod == WebConnection.HttpMethod.POST
			webConnection.requestParameters.size() == 1
			webConnection.requestParameters*.name == ['username']
	}

	def "should not save requestParameters without a name value"() {
		setup:
			def keyword = new Keyword(value:'AWESOME')
			def webConnection = new GenericWebConnection(name:"Ushahidi", keyword:keyword, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.POST)
			webConnection.addToRequestParameters(new RequestParameter(name:"name", value:'${name}'))
			webConnection.addToRequestParameters(new RequestParameter(name:"age", value:'${age}'))
			webConnection.save(failOnError:true)
			controller.params.ownerId = webConnection.id
			controller.params.name = "Ushahidi Connection"
			controller.params.keyword = "Test"
			controller.params.httpMethod = "post"
			controller.params.'param-name' = ""
			controller.params.type = "generic"
			controller.params.'param-value' = "geoffrey"
		when:
			controller.save()
		then:
			webConnection.name == "Ushahidi Connection"
			!webConnection.requestParameters
	}
}