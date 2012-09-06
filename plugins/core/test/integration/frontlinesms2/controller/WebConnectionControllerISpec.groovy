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

	def setup() {
		controller = new WebConnectionController()
	}

	def 'save action should also save a webconnection'(){
		setup:
			controller.params.name = "Test WebConnection"
			controller.params.httpMethod = "get"
			controller.params.url = "www.frontlinesms.com/sync"
			controller.params.keyword = "keyword"
		when:
			controller.save()
		then:
			WebConnection.findByName("Test WebConnection").name == controller.params.name
	}

	def 'save action should edit a webconnection and change the name'(){
		setup:
			def keyword = new Keyword(value:'TEST')
			def webConnection = new WebConnection(name:"Old WebConnection name", keyword:keyword, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			webConnection.save(failOnError:true)

			controller.params.ownerId = webConnection.id
			controller.params.name = "New WebConnection name"
			controller.params.url = "www.frontlinesms.com/sync"
			controller.params.httpMethod = "get"
			controller.params.keyword = "keyword"
		when:
			controller.save()
		then:
			WebConnection.get(controller.params.ownerId).name == "New WebConnection name"
	}
}