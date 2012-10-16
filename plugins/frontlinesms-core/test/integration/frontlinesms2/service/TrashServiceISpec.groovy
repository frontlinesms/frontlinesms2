package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class TrashServiceISpec extends grails.plugin.spock.IntegrationSpec {
	static transactional = false
	
	def trashService
	
	def cleanup() {
		['Fmessage', 'MessageOwner', 'PollResponse'].each { domainClass ->
			Fmessage.executeUpdate("DELETE FROM $domainClass")
		}
		
		assert !Fmessage.count()
		assert !MessageOwner.count()
		assert !PollResponse.count()
	}
	
	def "should permanently delete a poll and its messages when trashed"() {
		setup:
			def message = Fmessage.build(src:'123456', date:new Date(), inbound:true, isDeleted:false)
			message.save(failOnError:true, flush:true)
			def keyword = new Keyword(value: "FOOTBALL")
			def p = new Poll(name:'Who is the best football team in the world?', deleted:true).addToKeywords(keyword)
			p.editResponses(choiceA: "FC Manchester United", choiceB: "FC United of Manchester")
			p.save(failOnError:true, flush:true)
			PollResponse.findByValue("FC United of Manchester").addToMessages(message)
			p.save(failOnError:true, flush:true)
			assert Poll.count() == 1
			p.refresh()
			assert p.activityMessages.list()*.id == [message]*.id
		when:
			trashService.emptyTrash()
		then:
			Poll.count() == 0
			PollResponse.count() == 0
			Fmessage.count() == 0
			Trash.count() == 0
	}
	
	def "should permanently delete a folder and its messages when trashed"() {
		given:
			def message = Fmessage.build(src: '1234567', date: new Date(), inbound: true).save(failOnError:true, flush:true)
			def folder = new Folder(name:"test", deleted:true).save(failOnError:true, flush:true)
			folder.addToMessages(message)
			folder.save(failOnError:true, flush:true)
			assert Fmessage.count() == 1
			assert folder.getLiveMessageCount() == 1
		when:
			trashService.emptyTrash()
		then:
			Poll.count() == 0
			Fmessage.count() == 0
			Trash.count() == 0
	}

	def 'empty trash permanently deletes messages with isDeleted flag true'() {
		setup:
			(1..3).each {Fmessage.build(src:'123456', isDeleted:false, date:new Date(), inbound:true).save(failOnError:true, flush:true)}
			def inboxMessages = Fmessage.list()
			(1..3).each {Fmessage.build(src:'123456', isDeleted:true, date:new Date(), inbound:true).save(failOnError:true, flush:true)}
			
		when:
			trashService.emptyTrash()
		then:
			Fmessage.list() == inboxMessages
	}

	def 'should not be able to delete the same item twice'() {
		given: 'activity exists'
			def a = Announcement.build()
		when: 'activity deleted'
			trashService.sendToTrash(a)
		then: '1 trash item exists for activity'
			Trash.findAllByObjectIdAndObjectClass(a.id, a.class.name).size() == 1
		when: 'activity deleted again'
			trashService.sendToTrash(a)
		then: 'still only 1 trash item exists for activity'
			Trash.findAllByObjectIdAndObjectClass(a.id, a.class.name).size() == 1
	}
}
