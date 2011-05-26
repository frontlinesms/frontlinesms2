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

	private String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat()
		return formatedDate.format(date)
	}

	private DateFormat createDateFormat() {
		return new SimpleDateFormat("dd-MMM-yyyy")
	}
	
}
