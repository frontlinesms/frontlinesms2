package frontlinesms2

import org.grails.plugins.csv.CSVWriter

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

		Fmessage.findAll().each {
			csv << [id: it.id, src: it.src, dst: it.dst, text: it.text, dateCreated: it.dateCreated]
		}
		response.setHeader("Content-disposition", "attachment; filename=frontlineSMS-messageDump-${currentTime}.csv")
		render(contentType:"text/csv", text: csv.writer.toString(), encoding:"UTF-8")
	}
}
