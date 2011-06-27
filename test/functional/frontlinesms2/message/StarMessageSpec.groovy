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
			$('tr #star').click()
		then:
			message.refresh()
			$("tr #star").hasClass('starred')	
			assert message.starred
	}
	
	def 'clicking on a starred messages removes the "starred" CSS'() {
		when:
			message.addStar().save(failOnError: true, flush: true)
			go "message/inbox/show/$message.id"
			$('tr #star').click()
		then:
			message.refresh()
			assert !message.starred
			!$("tr #star").hasClass('starred')
			
	}
	
	def 'starring one message does not affect other messages'() {
		
	}
}

