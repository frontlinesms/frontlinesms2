package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class TrashServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def service = new TrashService()
	
	def "should permanently delete a poll and its messages when trashed"() {
		setup:
			def message = new Fmessage(src: '123456', date: new Date(), inbound: true).save(failOnError: true)
			def response1 = new PollResponse(value:"FC Manchester United")
			def response2 = new PollResponse(value:"FC United of Manchester")
			def p = new Poll(name:'Who is the best football team in the world?', keyword:"football", deleted:true)
			p.addToResponses(response1)
			p.addToResponses(response2)
			response2.addToMessages(message)
			p.save(failOnError:true)
		when:
			assert Poll.count() == 1
			assert p.getActivityMessages(false).count() == 1
			service.emptyTrash()
		then:
			Poll.count() == 0
			PollResponse.count() == 0
			Fmessage.count() == 0
			Trash.count() == 0
	}
	
	def "should permanently delete a folder and its messages when trashed"() {
		given:
			def message = new Fmessage(src: '1234567', date: new Date(), inbound: true).save(failOnError:true)
			def folder = new Folder(name:"test", deleted:true).save(failOnError:true)
			folder.addToMessages(message)
		when:
            assert Fmessage.count() == 1
			assert folder.getLiveMessageCount() == 1
			service.emptyTrash()
		then:
			Poll.count() == 0
			Fmessage.count() == 0
			Trash.count() == 0
	}
}