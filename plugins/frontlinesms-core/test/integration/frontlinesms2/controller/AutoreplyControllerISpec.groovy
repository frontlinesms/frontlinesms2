package frontlinesms2.controller

import frontlinesms2.*

import spock.lang.*

class AutoreplyControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def i18nUtilService

	def setup() {
		controller = new AutoreplyController()
	}
	
	@Unroll
	def 'can create an Autoreply'() {
		given:
			controller.params.name = name
			controller.params.keywords = keyword
			controller.params.messageText = autoreplyText
			controller.params.sorting = "enabled"
		when:
			controller.save()
		then:
			def autoreply = Autoreply.findByName(name)
			controller.flash.message == i18nUtilService.getMessage([code:"autoreply.save.success", args:[autoreply.name]])
			autoreply.autoreplyText == autoreplyText
			autoreply.keywords?.size() == 1
			autoreply.keywords[0].value == keyword
		where:
			name     | keyword | autoreplyText
			"Color"  | 'COLOR' | "ahhhhhhhhh"
			"Thanks" | ''      | "Thank you for the text"
	}
	
	@Unroll
	def "can edit an Autoreply"() {
		given: 'an autoreply exists'
			def k = new Keyword(value:initialKeyword)
			def a = new Autoreply(name:"Color", autoreplyText:"ahhhhhhhhh")
					.save(flush:true, failOnError:true)
			a.addToKeywords(k)
		and: 'controller params are setup'
			controller.params.ownerId = a.id
			controller.params.name = name
			controller.params.keywords = finalKeyword
			controller.params.sorting = "enabled"
			controller.params.messageText = autoreplyText
			controller.response.format = 'html'
			
		when:
			controller.save()

		then: 'the auto reply has been updated'
			def autoreply = Autoreply.findByName(name)
			autoreply.name == name
			autoreply.keywords[0].value == finalKeyword
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

	def "can create Autoreply with multiple keywords"(){
		given:
			controller.params.name = 'Fruit'
			controller.params.keywords = 'Mango,Orange,Banana'
			controller.params.messageText = 'Some Text'
			controller.response.format = 'html'
			controller.params.sorting = "enabled"
		when:
			controller.save()
		then:
			def autoreply = Autoreply.findByName("Fruit")
			autoreply.autoreplyText == 'Some Text'
			autoreply.keywords?.size() == 3
			autoreply.keywords[0].value == 'MANGO'
			autoreply.keywords[1].value == 'ORANGE'
			autoreply.keywords[2].value == 'BANANA'
	}

	def "can change Autoreply keywords"(){
		given: 'an autoreply exists'
			def a = new Autoreply(name:"Fruits", autoreplyText:"Hello")
			a.addToKeywords(new Keyword(value:"MANGO"))
			a.addToKeywords(new Keyword(value:"ORANGE"))
			a.save(flush:true, failOnError:true)
		and: 'controller params are setup'
			controller.params.ownerId = a.id
			controller.params.name = "Matunda"
			controller.params.keywords = "Apple,Strawberry"
			controller.params.messageText = "Hello"
			controller.response.format = 'html'
			controller.params.sorting = "enabled"
		when:
			controller.save()
		then: 'the auto reply has been updated'
			def autoreply = Autoreply.findByName("Matunda")
			autoreply.name == "Matunda"
			autoreply.keywords[0].value == "APPLE"
			autoreply.keywords[1].value == "STRAWBERRY"
			autoreply.autoreplyText == "Hello"
		and: 'the old keyword have been deleted'
			Keyword.findByValue("ORANGE") == null
	}

	def "can edit Autoreply with multiple keywords, keeping some of the old ones"(){
		given: 'an autoreply exists'
			def a = new Autoreply(name:"Fruits", autoreplyText:"Hello")
			a.addToKeywords(new Keyword(value:"MANGO"))
			a.addToKeywords(new Keyword(value:"ORANGE"))
			a.save(flush:true, failOnError:true)
		and: 'controller params are setup'
			controller.params.ownerId = a.id
			controller.params.name = "Matunda"
			controller.params.keywords = "Mango,Banana,Ovacado"
			controller.params.messageText = "Hello"
			controller.response.format = 'html'
			controller.params.sorting = "enabled"
		when:
			controller.save()
		then: 'the auto reply has been updated'
			def autoreply = Autoreply.findByName("Matunda")
			autoreply.name == "Matunda"
			autoreply.keywords[0].value == "MANGO"
			autoreply.keywords[1].value == "BANANA"
			autoreply.keywords[2].value == "OVACADO"
			autoreply.autoreplyText == "Hello"
		and: 'the old keyword have been deleted'
			Keyword.findByValue("ORANGE") == null
	}

	@Unroll
	def 'while editing an autoreply changing the sorting criteria should translate into proper keyword changes'(){
		setup:
			controller.params.name = "Matunda"
			controller.params.keywords = "Mango,Banana,Ovacado"
			controller.params.messageText = "Hello"
			controller.response.format = 'html'
		when:
			controller.params.sorting = sorting
			controller.save()
		then:
			results == Autoreply.findByName("Matunda").keywords*.value?.join(',')
		where:
			sorting|results
			"global"|''
			"enabled"|"MANGO,BANANA,OVACADO"
			"disabled"|null
	}

	def "can create Autoreply with global keyword"(){
		given:
			controller.params.name = 'Fruit'
			controller.params.keywords = ''
			controller.params.messageText = 'Some Text'
			controller.response.format = 'html'
			controller.params.sorting = "global"
		when:
			controller.save()
		then:
			def autoreply = Autoreply.findByName("Fruit")
			autoreply.autoreplyText == 'Some Text'
			autoreply.keywords?.size() == 1
			autoreply.keywords[0].value == ''
	}

	def "can create Autoreply without keywords"(){
		given:
			controller.params.name = 'Fruit'
			controller.params.keywords = ''
			controller.params.messageText = 'Some Text'
			controller.response.format = 'html'
			controller.params.sorting = "disabled"
		when:
			controller.save()
		then:
			def autoreply = Autoreply.findByName("Fruit")
			autoreply.autoreplyText == 'Some Text'
			autoreply.keywords == null
	}

	def 'restoring a deleted activity should fail if an activity with colliding keywords exists'(){
		setup:
			def trashService = new TrashService()
			def keyword = new Keyword(value:'TEAM')
			def autoreply = Autoreply.build(name:'Should fail restore')
			autoreply.addToKeywords(keyword)
			autoreply.save(failOnError:true)
			controller.params.id = autoreply.id
			trashService.sendToTrash(autoreply)
		expect:
			Autoreply.findByName('Should fail restore').deleted == true
		when:
			def keyword2 = new Keyword(value:'TEAM')
			def autoreply2 = Autoreply.build(name:'Keyword thief')
			autoreply2.addToKeywords(keyword2)
			autoreply2.save(failOnError:true)

			controller.params.id = autoreply.id
			controller.restore()
		then:
			controller.flash.message == "default.restore.failed[activity.label,${Autoreply.findByName("Should fail restore").id}]"
	}
}

