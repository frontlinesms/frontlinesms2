package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class TrashServiceISpec extends grails.plugin.spock.IntegrationSpec {
	static transactional = false
	
	def trashService
	
	def cleanup() {
		['TextMessage', 'MessageOwner', 'PollResponse'].each { domainClass ->
			TextMessage.executeUpdate("DELETE FROM $domainClass")
		}
		
		assert !TextMessage.count()
		assert !MessageOwner.count()
		assert !PollResponse.count()
	}
	
	def "should permanently delete a poll and its messages when trashed"() {
		setup:
			def message = TextMessage.build(src:'123456', date:new Date(), inbound:true, isDeleted:false)
			message.save(failOnError:true, flush:true)
			def keyword = new Keyword(value: "FOOTBALL")
			def p = new Poll(name:'Who is the best football team in the world?', deleted:true).addToKeywords(keyword)
			p.editResponses(choiceA: "FC Manchester United", choiceB: "FC United of Manchester")
			p.save(failOnError:true, flush:true)
			PollResponse.findByValue("FC United of Manchester").addToMessages(message)
			p.save(failOnError:true, flush:true)
			assert Poll.count() == 1
			p.refresh()
			assert p.activityMessages*.id == [message]*.id
		when:
			trashService.emptyTrash()
		then:
			Poll.count() == 0
			PollResponse.count() == 0
			TextMessage.count() == 0
			Trash.count() == 0
	}
	
	def "should permanently delete a folder and its messages when trashed"() {
		given:
			def message = TextMessage.build(src: '1234567', date: new Date(), inbound: true).save(failOnError:true, flush:true)
			def folder = new Folder(name:"test", deleted:true).save(failOnError:true, flush:true)
			folder.addToMessages(message)
			folder.save(failOnError:true, flush:true)
			assert TextMessage.count() == 1
			assert folder.getLiveMessageCount() == 1
		when:
			trashService.emptyTrash()
		then:
			Poll.count() == 0
			TextMessage.count() == 0
			Trash.count() == 0
	}

	def 'empty trash permanently deletes messages with isDeleted flag true'() {
		setup:
			(1..3).each {TextMessage.build(src:'123456', isDeleted:false, date:new Date(), inbound:true).save(failOnError:true, flush:true)}
			def inboxMessages = TextMessage.list()
			(1..3).each {TextMessage.build(src:'123456', isDeleted:true, date:new Date(), inbound:true).save(failOnError:true, flush:true)}
			
		when:
			trashService.emptyTrash()
		then:
			TextMessage.list() == inboxMessages
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

	def 'should be able to delete a very long message without trash service failing'() {
		given: 'message exists'
			def m = TextMessage.build(text: '''This message could go on and on and on and on and on and on and
					on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and 
					on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and 
					on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and 
					on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and 
					on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and on''')
		when: 'message deleted'
			trashService.sendToTrash(m)
		then: '1 trash item exists for message'
			Trash.findAllByObjectIdAndObjectClass(m.id, m.class.name).size() == 1
			Trash.findByObjectIdAndObjectClass(m.id, m.class.name).displayText == '''This message could go on and on and on and on and on and on and
					on and on and on and on and on and on and on and on and on and on and on and on and on and on and on and 
					on and on and on and on and on and on and on and on and on and on and on a…'''
	}
}
