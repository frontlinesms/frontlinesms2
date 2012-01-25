package frontlinesms2.controller

import spock.lang.*
import frontlinesms2.*
import grails.plugin.spock.*

class DeleteISpec extends IntegrationSpec {
	def "deleted polls are not included in the pollInstanceList"() {
		given:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, date: new Date()).save()
			def message2 = new Fmessage(src:'Alice', dst:'+2541234567', text:'go barcelona', inbound:true, date: new Date()).save()
			def p = Poll.createPoll(title: 'This is a poll', choiceA: 'Manchester', choiceB:'Barcelona').save(failOnError:true, flush:true)
			def messageController = new MessageController()
			def pollController = new PollController()
			PollResponse.findByValue('Manchester').addToMessages(message1).save(failOnError: true)
			PollResponse.findByValue('Barcelona').addToMessages(message2).save(failOnError: true)
			p.save(flush:true, failOnError:true)
		when:
			messageController.beforeInterceptor()
			def model1 = messageController.getShowModel()
		then:
			model1.pollInstanceList == [p]
		when:
			pollController.params.id = p.id
			pollController.delete()
			messageController.beforeInterceptor()
			def model2 = messageController.getShowModel()
		then:
			!model2.pollInstanceList
	}
	
	def "deleted polls are not included in the polls list"() {
		given:
			def message1 = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', inbound:true, date: new Date()).save()
			def message2 = new Fmessage(src:'Alice', dst:'+2541234567', text:'go barcelona', inbound:true, date: new Date()).save()
			def p = Poll.createPoll(title: 'This is a poll', choiceA: 'Manchester', choiceB:'Barcelona').save(failOnError:true, flush:true)
			def pollController = new PollController()
			PollResponse.findByValue('Manchester').addToMessages(message1).save(failOnError: true)
			PollResponse.findByValue('Barcelona').addToMessages(message2).save(failOnError: true)
			p.save(flush:true, failOnError:true)
		when:
			pollController.params.viewingArchive = false
			def model1 = pollController.index()
		then:
			model1.polls == [p]
		when:
			pollController.params.id = p.id
			pollController.delete()
			pollController.params.viewingArchive = false
			def model2 = pollController.index()
		then:
			!model2.polls
	}
	
	def "deleted folders are not included in the folderInstanceList"() {
		given:
			def f = new Folder(name:'test').save(failOnError:true)
			def m = new Fmessage(src: '123456', date: new Date(), inbound: true)
			def messageController = new MessageController()
			def folderController = new FolderController()
			f.addToMessages(m)
			f.save(flush:true, failOnError:true)
		when:
			messageController.beforeInterceptor()
			def model1 = messageController.getShowModel()
		then:
			model1.folderInstanceList == [f]
		when:
			folderController.params.id = f.id
			folderController.delete()
			messageController.beforeInterceptor()
			def model2 = messageController.getShowModel()
		then:
			!model2.folderInstanceList
	}
	
	def "deleted folders are included in the trash list"() {
		given:
			def f = new Folder(name:'test').save(failOnError:true)
			def m = new Fmessage(src: '12345', inbound: true, date: new Date())
			def m2 = new Fmessage(src: '12345', inbound: true, date: new Date()-1)
			def messageController = new MessageController()
			f.addToMessages(m)
			f.addToMessages(m2)
			deleteFolder(f)
			f.save(flush:true, failOnError:true)
		when:
			messageController.beforeInterceptor()
			messageController.trash()
			def model = messageController.modelAndView.model.trashInstanceList
		then:
			model.collect {it.link} == [f]
	}
	
	def "polls, folders and messages appear in the trash section"() {
		given:
			def f = new Folder(name:'test').save(failOnError:true)
			def m = new Fmessage(src: '12345', inbound: true, date: new Date())
			def m2 = new Fmessage(src: '12345', inbound: true, date: new Date()-1)
			def messageController = new MessageController()
			f.addToMessages(m)
			f.addToMessages(m2)
			deleteFolder(f)
			f.save(flush:true, failOnError:true)
			
			def message1 = new Fmessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save()
			def message2 = new Fmessage(src:'Alice', text:'go barcelona', inbound:true, date: new Date()).save()
			
			def m3 = new Fmessage(src: '1235', text:"not in folder", isDeleted: true, date: new Date(), inbound: true).save(flush:true, failOnError:true)
			deleteMessage(m3)
			def p = Poll.createPoll(title: 'This is a poll', choiceA: 'Manchester', choiceB:'Barcelona').save(failOnError:true, flush:true)
			PollResponse.findByValue('Manchester').addToMessages(message1).save(failOnError: true)
			PollResponse.findByValue('Barcelona').addToMessages(message2).save(failOnError: true)
			deletePoll(p)
			p.save(flush:true, failOnError:true)
		when:
			messageController.beforeInterceptor()
			messageController.trash()
			def model = messageController.modelAndView.model.trashInstanceList
		then:
			model.collect {it.link} == [p, m3, f]
		}
	
	def deleteMessage(Fmessage message) {
		message.isDeleted = true
		message.save()
		new Trash(identifier:message.displayName, message:message.text, objectType:message.class.name, linkId:message.id).save(failOnError: true, flush: true)
	}
	
	def deleteFolder(Folder folder) {
		folder.deleted = true
		folder.save()
		new Trash(identifier:folder.name, message:"${folder.liveMessageCount}", objectType:folder.class.name, linkId:folder.id).save(failOnError: true, flush: true)
	}
	
	def deletePoll(Poll poll){
		poll.deleted = true
		poll.save()
		new Trash(identifier:poll.title, message:"${poll.liveMessageCount}", objectType:poll.class.name, linkId:poll.id).save(failOnError: true, flush: true)
	}
}

