package frontlinesms2.webconnection

import frontlinesms2.*
import frontlinesms2.message.*
import frontlinesms2.popup.*

abstract class WebconnectionBaseSpec extends grails.plugin.geb.GebSpec {
	static createWebconnections() {
		remote {
			def syncKeyword = new Keyword(value:"SYNC")
			def ushKeyword = new Keyword(value:"USH")
			def keyword = new Keyword(value:"TRIAL")
			new GenericWebconnection(name:"Sync", url:"http://www.frontlinesms.com/sync", httpMethod:Webconnection.HttpMethod.GET).addToKeywords(syncKeyword).save(failOnError:true, flush:true)
			new UshahidiWebconnection(name:"Trial", url:"https://trial.crowdmap.com/frontlinesms/", httpMethod:Webconnection.HttpMethod.POST).addToKeywords(keyword).save(failOnError:true, flush:true)
			new UshahidiWebconnection(name:"Ush", url:"http://www.ushahidi.com/frontlinesms", httpMethod:Webconnection.HttpMethod.GET).addToKeywords(ushKeyword).save(failOnError:true, flush:true)
			null
		}
	}

	static createTestActivities() {
		remote {
			Announcement.build(name:"Sample Announcement", sentMessageText:"Message to send")
			Fmessage.build(src:'announce')
			null
		}
	}

	static createTestMessages(Webconnection wc) {
		remote {
			(0..90).each {
				def m = Fmessage.build(src:'Bob', text:"Test message $it", date:new Date()-it)
				it % 5 == 0 ? (m.setMessageDetail(wc, DispatchStatus.SENT)) : (m.setMessageDetail(wc, DispatchStatus.FAILED))
				wc.addToMessages(m)
			}
			wc.save(flush:true, failOnError:true)
			null
		}
	}

	protected def launchWizard(webconnectionType=null) {
		to PageMessageInbox
		bodyMenu.newActivity.click()
		waitFor { at CreateActivityDialog }
		webconnection.click()
		at WebconnectionWizard

		if(!webconnectionType) {
			waitFor { option('generic').displayed }
			return true
		}

		option(webconnectionType).click()
		next.click()

		if(webconnectionType == "ushahidi") {
			waitFor { configureUshahidi.subType('crowdmap').displayed }
		} else if(webconnectionType == "generic") {
			waitFor { requestTab.post.displayed }
		}

		return true
	}
}

