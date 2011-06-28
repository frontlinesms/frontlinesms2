package frontlinesms2

import org.grails.plugins.csv.CSVWriter
import java.text.DateFormat
import java.text.SimpleDateFormat

class ReportController {

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
	
	def generateCSVReport(Collection<Fmessage> model = []) {
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
		model.each {
			csv << [id: it.id, src: it.src, dst: it.dst, text: it.text, dateCreated: it.dateCreated]
		}
		response.setHeader("Content-disposition", "attachment; filename=frontlineSMS-searchReport-${formatedTime}.csv")
		render(contentType:"text/csv", text: csv.writer.toString(), encoding:"UTF-8")
	}
	
	def generatePDFReport(Collection<Fmessage> model = []) {
		def currentTime = new Date()
		def exportService
		
		List fields = ["id", "src", "dst", "text", "dateCreated"]
		Map labels = ["id":"DatabaseID", "src":"Source", "dst":"Destination", "text":"Text", "dateReceived":"Date"]

		def formatedTime = dateToString(currentTime)
		Map parameters = [title: "frontlineSMS-searchReport", "column.widhts": [0.2, 0.3, 0.5]]
		response.setHeader("Content-disposition", "attachment; filename=frontlineSMS-searchReport-${formatedTime}.pdf")
		
		println "model: $model"
		exportService.export("application/pdf", response.outputStream, model.findAll{}, fields, labels, parameters)
		
		[searchResultsList: model]
	}

	private String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat()
		return formatedDate.format(date)
	}

	private DateFormat createDateFormat() {
		return new SimpleDateFormat("dd-MMM-yyyy")
	}
	
}
