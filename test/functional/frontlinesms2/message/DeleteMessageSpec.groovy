package frontlinesms2.message

import frontlinesms2.*

class DeleteMessageSpec extends grails.plugin.geb.GebSpec {
		
	def 'deleted messages do not show up in any view'() {
		given:
			createTestData()
			assert Fmessage.getSentMessages().size() == 2
			assert Fmessage.getInboxMessages().size() == 3
			assert Poll.findByTitle('Miauow Mix').messages.size() == 2
		when:
			go "message/deleteMessage/${Fmessage.findBySrc('Bob').id}"
			go "message/deleteMessage/${Fmessage.findBySrc('Mary').id}"
			go "message/deleteMessage/${Fmessage.findBySrc('Barnabus').id}"
		then:
			Fmessage.getSentMessages().size() == 1
			Fmessage.getInboxMessages().size() == 2
			Poll.findByTitle('Miauow Mix').messages.size() == 1
		cleanup:
			deleteTestData()
	}
	
	def 'delete button appears in message show view and works'() {
		given:
			createTestData()
			def bob = Fmessage.findBySrc("Bob")
		when:
			go "message/inbox/show/${bob.id}"
			def btnDelete = $('#message-details .buttons a')
		then:
			btnDelete
		when:
			btnDelete.click()
		then:
			at MessagesPage
		when:
			bob.refresh()
		then:
			bob.deleted
		cleanup:
			deleteTestData()
	}
	
	static createTestData() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test')].each() {
					it.inbound = true
					it.save(failOnError:true)
				}
		[new Fmessage(src:'Mary', dst:'+254112233', text:'hi Mary'),
				new Fmessage(src:'+254445566', dst:'+254112233', text:'test')].each() {
					it.inbound = false
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', inbound:true)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver', inbound:false)
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
	}
	
	static deleteTestData() {

		Poll.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}

		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}

