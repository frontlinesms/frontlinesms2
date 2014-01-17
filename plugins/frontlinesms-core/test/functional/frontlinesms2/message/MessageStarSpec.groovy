package frontlinesms2.message

import frontlinesms2.*

class MessageStarSpec extends grails.plugin.geb.GebSpec{
		
	def setup() {
		remote {
			new TextMessage(src:'+254287645', dst:'+254112233', text:'css test', inbound:true, read:false).save(failOnError:true, flush:true)
			null
		}
	}
	
	def 'clicking on an unstarred message changes its CSS to starred'() {
		setup:
			def m = remote { TextMessage.findBySrc('+254287645').id }
		when:
			to PageMessageInbox
			messageList.starFor(m).click()
		then:
			waitFor { messageList.starFor(m).hasClass("starred") }
			remote { TextMessage.findBySrc('+254287645').starred }
	}
	
	def 'clicking on a starred messages removes the starred CSS'() {
		given:
			def message = remote {
				def m = TextMessage.findBySrc('+254287645')
				m.starred = true
				m.save(flush:true)
				m.id
			}
		when:
			to PageMessageInbox, message
			messageList.starFor(message).click()
		then:
			waitFor { messageList.starFor(message).hasClass("unstarred") }
			remote { !TextMessage.get(message).starred }
			
	}
	
	def 'starring one message does not affect other messages'() {
		when:
			def ids = remote {
				[new TextMessage(src:'+254556677', dst:'+254112233', text:'css test 2', inbound:true, read:false).save(failOnError:true, flush:true),
					TextMessage.findBySrc('+254287645')]*.id
			}
			def id0 = ids[0]
			def id1 = ids[1]
			to PageMessageInbox, id1
			messageList.starFor(id1).click()
		then:
			waitFor { messageList.starFor(id1).hasClass("starred") }
			remote { TextMessage.findBySrc('+254287645').starred }
			!messageList.starFor(id0).hasClass('starred')
			remote { !TextMessage.get(id0).starred }
			remote { !TextMessage.findBySrc('+254556677').starred }
	}
}

