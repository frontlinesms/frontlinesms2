package frontlinesms2.webconnection

import frontlinesms2.*

class WebConnectionBaseSpec extends grails.plugin.geb.GebSpec {

	static createWebConnections() {
		def connection = new HttpWebConnectionFconnection(name:"testConn", url:"http://www.frontlinesms.com/sync", httpMethod:HttpWebConnectionFconnection.HttpMethod.GET)
		def syncKeyword = new Keyword(value:"SYNC")
		new WebConnection(name:"Sync", keyword:syncKeyword, connection:connection).save(failOnError:true)
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
	}
}