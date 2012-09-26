package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

abstract class WebConnectionBaseSpec extends grails.plugin.geb.GebSpec {
	static createWebConnections() {
		def syncKeyword = new Keyword(value:"SYNC")
		new GenericWebConnection(name:"Sync", keyword:syncKeyword, url:"http://www.frontlinesms.com/sync", httpMethod:WebConnection.HttpMethod.GET).save(failOnError:true)
	}

	static createTestActivities() {
		Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
		Fmessage.build(src:'announce')
	}

	static createTestMessages(WebConnection wc) {
		(0..90).each {
			def m = Fmessage.build(src:'Bob', text:"Test message $it", date:new Date()-it)
			wc.addToMessages(m)
			wc.save(failOnError:true)
		}
		wc.save(flush:true, failOnError:true)
	}

	protected def launchWizard(webConnectionType=null) {
		to PageMessageInbox
		bodyMenu.newActivity.click()
		waitFor { at CreateActivityDialog }
		webconnection.click()
		waitFor('slow') { at WebConnectionWizard }
		if(webConnectionType) {
			// TODO select option and click next
		}
		return true
	}
}
