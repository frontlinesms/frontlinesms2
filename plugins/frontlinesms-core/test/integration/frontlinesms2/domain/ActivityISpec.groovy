package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*
import grails.plugin.spock.*

class ActivityISpec extends grails.plugin.spock.IntegrationSpec {
	def trashService

	def 'restoring a deleted activity should fail if an activity with colliding keywords exists'(){
		setup:
			def keyword = new Keyword(value:'TEAM')
			def autoreply = Autoreply.build(name:'Should fail restore')
			autoreply.addToKeywords(keyword)
			autoreply.save(failOnError:true)
		when:
			trashService.sendToTrash(autoreply)
		then:
			Autoreply.findByName('Should fail restore').deleted == true
		when:
			def keyword2 = new Keyword(value:'TEAM')
			def autoreply2 = Autoreply.build(name:'Keyword thief')
			autoreply2.addToKeywords(keyword2)
			autoreply2.save(failOnError:true)
			trashService.restore(Autoreply.findByName('Should fail restore'))
		then:'Restore should fail ## object.deleted == true'
			Autoreply.findByName('Should fail restore').deleted == true
	}
}