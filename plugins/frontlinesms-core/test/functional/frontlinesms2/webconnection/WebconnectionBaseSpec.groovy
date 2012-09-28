package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

abstract class WebconnectionBaseSpec extends grails.plugin.geb.GebSpec {
	static createWebconnections() {
		def syncKeyword = new Keyword(value:"SYNC")
		def ushKeyword = new Keyword(value:"USH")
		def keyword = new Keyword(value:"TRIAL")
		new GenericWebconnection(name:"Sync", keyword:syncKeyword, url:"http://www.frontlinesms.com/sync", httpMethod:Webconnection.HttpMethod.GET).save(failOnError:true)
		new UshahidiWebconnection(name:"Trial", keyword:keyword, url:"www.ushahidi.com/frontlinesms2", httpMethod:Webconnection.HttpMethod.POST).save(failOnError:true)
		new UshahidiWebconnection(name:"Ush", keyword:ushKeyword, url:"http://www.ushahidi.com/frontlinesms", httpMethod:Webconnection.HttpMethod.GET).save(failOnError:true)
	}

	static createTestActivities() {
		Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
		Fmessage.build(src:'announce')
	}

	static createTestMessages(Webconnection wc) {
		(0..90).each {
			def m = Fmessage.build(src:'Bob', text:"Test message $it", date:new Date()-it)
			wc.addToMessages(m)
			wc.save(failOnError:true)
		}
		wc.save(flush:true, failOnError:true)
	}

	protected def launchWizard(webconnectionType=null) {
		to PageMessageInbox
		bodyMenu.newActivity.click()
		waitFor { at CreateActivityDialog }
		webconnection.click()
		at WebconnectionWizard
		if(webconnectionType) {
			selectWebconnectionType.option(webconnectionType)
			next.click()
		}
		return true
	}
}
