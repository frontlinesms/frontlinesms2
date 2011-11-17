package frontlinesms2.message

import frontlinesms2.*

class MessageStarSpec extends grails.plugin.geb.GebSpec{
		
	def setup() {
		new Fmessage(src:'+254287645', dst:'+254112233', text:'css test', status: MessageStatus.INBOUND, read: false).save(failOnError:true)
	}
	
	def 'clicking on an unstarred message changes its CSS to starred'() {
		when:
			go "message/inbox/show/${Fmessage.findBySrc('+254287645').id}"
			$("tr #star-${Fmessage.findBySrc('+254287645').id} a").click()
		then:
			waitFor {$("tr #star-${Fmessage.findBySrc('+254287645').id} a").hasClass("starred")}
			Fmessage.findBySrc('+254287645').refresh()
			Fmessage.findBySrc('+254287645').starred
	}
	
	def 'clicking on a starred messages removes the starred CSS'() {
		when:
			Fmessage.findBySrc('+254287645').addStar().save(failOnError: true, flush: true)
			go "message/inbox/show/${Fmessage.findBySrc('+254287645').id}"
			$("tr #star-${Fmessage.findBySrc('+254287645').id} a").click()
		then:
			waitFor {$("tr #star-${Fmessage.findBySrc('+254287645').id} a").hasClass("unstarred")}
			Fmessage.findBySrc('+254287645').refresh()
			!Fmessage.findBySrc('+254287645').starred
			
	}
	
	def 'starring one message does not affect other messages'() {
		when:
			new Fmessage(src:'+254556677', dst:'+254112233', text:'css test 2', status: MessageStatus.INBOUND, read: false).save(failOnError:true)
			go "message/inbox/show/${Fmessage.findBySrc('+254287645').id}"
			$("tr #star-${Fmessage.findBySrc('+254287645').id} a").click()
		then:
			waitFor {$("tr #star-${Fmessage.findBySrc('+254287645').id} a").hasClass("starred")}
			Fmessage.findBySrc('+254287645').refresh()
			Fmessage.findBySrc('+254287645').starred
			!$("tr #star-${Fmessage.findBySrc('+254556677').id}").hasClass('starred')	
			!Fmessage.findBySrc('+254556677').starred
	
	}
}

