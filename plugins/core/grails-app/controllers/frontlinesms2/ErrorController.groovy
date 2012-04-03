package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat

class ErrorController {
	static final def DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")

	def logs = { supplyDownload('log') }

	def logsAndDatabase = {	supplyDownload('log-and-database', null) }

	private def supplyDownload(filename, filter={ file -> file.name.endsWith('.log') }) {
		def formatedDate = DATE_FORMAT.format(new Date())
		response.setContentType("application/x-zip-compressed")
		response.setHeader("Content-disposition", "attachment; filename=frontlinesms2-$filename-${formatedDate}.zip")
		new File("${System.properties.'user.home'}/.frontlinesms2/").zip(response.outputStream, filter)
		response.outputStream.flush()
	}
	
	def createException = {
		throw new RuntimeException("This exception was generated at the user's request.",
				new RuntimeException("And here is a nested exception ;¬)"))
	}
}
