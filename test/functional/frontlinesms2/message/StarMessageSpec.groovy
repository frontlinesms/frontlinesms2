package frontlinesms2.message

import frontlinesms2.*

class StarMessageSpec extends MessageGebSpec{
		def message
		
		def setup() {
			message = new Fmessage(src:'+254223344', dst:'+254112233', text:'css test', inbound: true, read: false).save(failOnError:true)
		}
		def cleanup() {
			deleteTestMessages()
		}
		
		def 'clicking on an unstarred message changes its CSS to "starred"'() {
		when:
			go "message/inbox/show/$message.id"
			$('#star').click()
		then:
			$("tr#message-${message.id} #star").hasClass('starred')	
			assert message.starred
	}
	
	def 'clicking on a starred messages removes the "starred" CSS'() {
		when:
			message.addStar()
			go "message/inbox/show/$message.id"
			$('#star').click()
		then:
			assert !message.starred
			!$("tr#message-${message.id} #star").hasClass('starred')
			
	}
}

