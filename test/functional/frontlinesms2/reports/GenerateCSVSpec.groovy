package frontlinesms2.reports

import frontlinesms2.*
import org.grails.plugins.csv.*
import frontlinesms2.enums.MessageStatus

class GenerateCSVSpec extends grails.plugin.geb.GebSpec {
	def 'export all messages as CSV file exists' () {
		when:
			to ReportsPage
			def btnCreateReport = $('#create-CSVreport a')
		then:
			btnCreateReport.getAttribute('href') == "/frontlinesms2/report/create"
	}

	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test')].each() {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true)
				}
	}

	static deleteTestMessages() {
		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}

class ReportsPage extends geb.Page {
	static url = 'report'
}
