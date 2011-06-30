package frontlinesms2

import org.grails.plugins.csv.CSVWriter
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import java.text.DateFormat
import java.text.SimpleDateFormat

class ReportController {

	def exportService
	
    def index = { }

	def create = {
		def currentTime = new Date()
		def swriter = new StringWriter()
		def csv = new CSVWriter(swriter, {
			DatabaseID {it.id}
			Source {it.src}
			Destination {it.dst}
			Text {it.text}
			Date {it.dateCreated}
		})

		def formatedTime = dateToString(currentTime)
		Fmessage.findAll().each {
			csv << [id: it.id, src: it.src, dst: it.dst, text: it.text, dateCreated: it.dateCreated]
		}
		response.setHeader("Content-disposition", "attachment; filename=frontlineSMS-messageDump-${formatedTime}.csv")
		render(contentType:"text/csv", text: csv.writer.toString(), encoding:"UTF-8")
	}
	
	def generateCSVReport(model) {
		def currentTime = new Date()
		List fields = ["id", "src", "dst", "text", "dateCreated"]
		Map labels = ["id":"DatabaseID", "src":"Source", "dst":"Destination", "text":"Text", "dateReceived":"Date"]

		def formatedTime = dateToString(currentTime)
		response.setHeader("Content-disposition", "attachment; filename=frontlineSMS-searchReport-${formatedTime}.csv")
		exportService.export(params.format, response.outputStream, model, fields, labels, [:],[:])
	}
	
	def generatePDFReport(model) {
		def currentTime = new Date()
		List fields = ["id", "src", "dst", "text", "dateCreated"]
		Map labels = ["id":"DatabaseID", "src":"Source", "dst":"Destination", "text":"Text", "dateReceived":"Date"]
		println "List: ${model}"
		def formatedTime = dateToString(currentTime)
		Map parameters = [title: "frontlineSMS-searchReport", "column.widhts": [0.2, 0.3, 0.5]]
		response.setHeader("Content-disposition", "attachment; filename=frontlineSMS-searchReport-${formatedTime}.pdf")
		exportService.export(params.format, response.outputStream, model, fields, labels, [:],[:])
		
		[messageInstanceList: model]
	}

	private String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat()
		return formatedDate.format(date)
	}

	private DateFormat createDateFormat() {
		return new SimpleDateFormat("dd-MMM-yyyy")
	}
	
}
