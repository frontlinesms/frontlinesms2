package frontlinesms2.message

import frontlinesms2.*

class MessageStarSpec extends grails.plugin.geb.GebSpec{
		
	def setup() {
		remote {
			new Fmessage(src:'+254287645', dst:'+254112233', text:'css test', inbound:true, read:false).save(failOnError:true, flush:true)
			null
		}
	}
	
	def 'clicking on an unstarred message changes its CSS to starred'() {
		setup:
			def m = Fmessage.findBySrc('+254287645')
		when:
			to PageMessageInbox
			messageList.starFor(m).click()
		then:
			waitFor {messageList.starFor(m).hasClass("starred")}
			remote { Fmessage.findBySrc('+254287645').starred }
	}
	
	def 'clicking on a starred messages removes the starred CSS'() {
		given:
			def message = Fmessage.findBySrc('+254287645')
			message.starred = true
			message.save(flush:true)
		when:
			to PageMessageInbox, message.id
			messageList.starFor(message).click()
		then:
			waitFor {messageList.starFor(message).hasClass("unstarred")}
			remote { !message.starred }
			
	}
	
	def 'starring one message does not affect other messages'() {
		when:
			def id = remote {
				new Fmessage(src:'+254556677', dst:'+254112233', text:'css test 2', inbound:true, read:false).save(failOnError:true, flush:true)
				Fmessage.findBySrc('+254287645').id
			}
			to PageMessageInbox, id
			messageList.starFor(m2).click()
		then:
			waitFor { messageList.starFor(m2).hasClass("starred") }
			remote { Fmessage.findBySrc('+254287645').starred }
			!messageList.starFor(m1).hasClass('starred')	
			!m1.starred
			remote { !Fmessage.findBySrc('+254556677').starred }
	}
}

