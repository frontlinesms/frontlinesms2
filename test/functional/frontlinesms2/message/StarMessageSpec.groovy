package frontlinesms2.message

import frontlinesms2.*

class StarMessageSpec extends MessageGebSpec{
		
	def setup() {
		new Fmessage(src:'+254223344', dst:'+254112233', text:'css test', inbound: true, read: false).save(failOnError:true)
		new Fmessage(src:'+254556677', dst:'+254112233', text:'css test 2', inbound: true, read: false).save(failOnError:true)
	}
	
	def cleanup() {
		deleteTestMessages()
	}
		
	def 'clicking on an unstarred message changes its CSS to "starred"'() {
		when:
			go "message/inbox/show/${Fmessage.findBySrc('+254223344').id}"
			$("tr #star-${Fmessage.findBySrc('+254223344').id}").click()
		then:
			Fmessage.findBySrc('+254223344').refresh()
			$("tr #star-${Fmessage.findBySrc('+254223344').id}").hasClass('starred')	
			assert Fmessage.findBySrc('+254223344').starred
	}
	
	def 'clicking on a starred messages removes the "starred" CSS'() {
		when:
			Fmessage.findBySrc('+254223344').addStar().save(failOnError: true, flush: true)
			go "message/inbox/show/${Fmessage.findBySrc('+254223344').id}"
			$("tr #star-${Fmessage.findBySrc('+254223344').id}").click()
		then:
			Fmessage.findBySrc('+254223344').refresh()
			assert !Fmessage.findBySrc('+254223344').starred
			!$("tr #star-${Fmessage.findBySrc('+254223344').id}").hasClass('starred')
			
	}
	
	def 'starring one message does not affect other messages'() {
		when:
			go "message/inbox/show/${Fmessage.findBySrc('+254223344').id}"
			$("tr #star-${Fmessage.findBySrc('+254223344').id}").click()
		then:
			Fmessage.findBySrc('+254223344').refresh()
			$("tr #star-${Fmessage.findBySrc('+254223344').id}").hasClass('starred')	
			assert Fmessage.findBySrc('+254223344').starred
			
			!$("tr #star-${Fmessage.findBySrc('+254556677').id}").hasClass('starred')	
			assert !Fmessage.findBySrc('+254556677').starred
	
	}
}

