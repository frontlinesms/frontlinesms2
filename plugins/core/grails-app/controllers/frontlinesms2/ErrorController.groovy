package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat

class ErrorController {
	static final def DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")

	def zip_to_download = {
		def formatedDate = DATE_FORMAT.format(new Date())
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=frontlinesms2-log-${formatedDate}.zip")
		new File("${System.properties.'user.home'}/.frontlinesms2/").zip(response.outputStream)
		response.outputStream.flush()
	}
	
	def createException = {
		throw new RuntimeException("This exception was generated at the user's request.", new RuntimeException("And here is a nested exception ;-)"))
	}
}
