package frontlinesms2.controller

import frontlinesms2.*

import spock.lang.*

class AutoreplyControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new AutoreplyController()
	}
	
	@Unroll
	def 'can create an Autoreply'() {
		given:
			controller.params.name = name
			controller.params.keyword = keyword
			controller.params.autoreplyText = autoreplyText
		when:
			controller.save()
		then:
			def autoreply = Autoreply.findByName(name)
			autoreply.autoreplyText == autoreplyText
			autoreply.keyword.value == keyword
		where:
			name     | keyword | autoreplyText
			"Color"  | 'COLOR' | "ahhhhhhhhh"
			"Thanks" | ''      | "Thank you for the text"
	}
	
	@Unroll
	def "can edit an Autoreply"() {
		given: 'an autoreply exists'
			def k = new Keyword(value:initialKeyword)
			def a = new Autoreply(name:"Color", keyword:k, autoreplyText:"ahhhhhhhhh")
			a.save(flush:true, failOnError:true)
			
		and: 'controller params are setup'
			controller.params.ownerId = a.id
			controller.params.name = name
			controller.params.keyword = finalKeyword
			controller.params.autoreplyText = autoreplyText
			controller.params.format = "html"
			
		when:
			def model = controller.save()
			
		then: 'the auto reply has been updated'
			def autoreply = Autoreply.get(model.ownerId)
			autoreply.name == name
			autoreply.keyword.value == finalKeyword
			autoreply.autoreplyText == autoreplyText
		
		and: 'the old auto reply and keyword have been deleted'
			Keyword.findByValue(initialKeyword) == null
			Autoreply.findByName("Color") == null
			
		where:
			name      | initialKeyword | finalKeyword | autoreplyText
			"ColorZ"  | "COLOR"        | "COLORZ"     | "blue, i mean green"
			"Blank"   | "COLOR"        | ""           | "blue, i mean green"
			"ColorZ"  | ""             | "COLORZ"     | "blue, i mean green"
	}  
}
