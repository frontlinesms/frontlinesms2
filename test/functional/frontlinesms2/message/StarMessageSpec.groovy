package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class StarMessageSpec extends grails.plugin.geb.GebSpec{
		
	def setup() {
		new Fmessage(src:'+254287645', dst:'+254112233', text:'css test', status: MessageStatus.INBOUND, read: false).save(failOnError:true)
	}
	
	def cleanup() {
		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
		
	def 'clicking on an unstarred message changes its CSS to starred'() {
		when:
			go "message/inbox/show/${Fmessage.findBySrc('+254287645').id}"
			$("tr #star-${Fmessage.findBySrc('+254287645').id}").click()
			waitFor {$("tr #star-${Fmessage.findBySrc('+254287645').id}").text() == "Remove Star"}
			Fmessage.findBySrc('+254287645').refresh()
		then:
			println "Messages: ${Fmessage.findAll()}"
			Fmessage.findBySrc('+254287645').starred
			$("tr #star-${Fmessage.findBySrc('+254287645').id}").hasClass('starred')
	}
	
	def 'clicking on a starred messages removes the starred CSS'() {
		when:
			Fmessage.findBySrc('+254287645').addStar().save(failOnError: true, flush: true)
			go "message/inbox/show/${Fmessage.findBySrc('+254287645').id}"
			$("tr #star-${Fmessage.findBySrc('+254287645').id}").click()
			waitFor {$("tr #star-${Fmessage.findBySrc('+254287645').id}").text() == "Add Star"}
			Fmessage.findBySrc('+254287645').refresh()
		then:
			!Fmessage.findBySrc('+254287645').starred
			!$("tr #star-${Fmessage.findBySrc('+254287645').id}").hasClass('starred')
			
	}
	
	def 'starring one message does not affect other messages'() {
		when:
			new Fmessage(src:'+254556677', dst:'+254112233', text:'css test 2', status: MessageStatus.INBOUND, read: false).save(failOnError:true)
			go "message/inbox/show/${Fmessage.findBySrc('+254287645').id}"
			$("tr #star-${Fmessage.findBySrc('+254287645').id}").click()
			waitFor {$("tr #star-${Fmessage.findBySrc('+254287645').id}").text() == "Remove Star"}
		then:
			Fmessage.findBySrc('+254287645').refresh()
			$("tr #star-${Fmessage.findBySrc('+254287645').id}").hasClass('starred')	
			assert Fmessage.findBySrc('+254287645').starred
			
			!$("tr #star-${Fmessage.findBySrc('+254556677').id}").hasClass('starred')	
			assert !Fmessage.findBySrc('+254556677').starred
	
	}
}

