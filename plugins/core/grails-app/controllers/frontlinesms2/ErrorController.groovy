package frontlinesms2

import java.util.zip.*
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat

class ErrorController {

    def zip_to_download = {
		createZipFile(response.outputStream)
		def formatedDate = dateToString(new Date())
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=frontlinesms2-log-${formatedDate}")
		new File("${System.properties.'user.home'}/.frontlinesms2/").zip()
		response.outputStream.flush()
	}
	
	def createException = {
		def formatedDate = dateToString(new Date())
		createZipFile("${System.properties.'user.home'}/.frontlinesms2/frontlinesms2-log-${formatedDate}")
		new File("${System.properties.'user.home'}/.frontlinesms2/").zip()
	}
	
	private def createZipFile(output) {
		File.metaClass.zip = {
			def result = new ZipOutputStream(output)
			result.withStream {zipOutStream->
				delegate.eachFileRecurse { f ->
					if(!f.isDirectory()) {
						zipOutStream.putNextEntry(new ZipEntry(f.getPath()))
						new FileInputStream(f).withStream { inStream ->
							def buffer = new byte[1024]
							def count
							while((count = inStream.read(buffer, 0, 1024)) != -1) {
								zipOutStream.write(buffer)
							}
						}
						zipOutStream.closeEntry()
					}
				}
			}
		}
	}
	
	private String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat()
		return formatedDate.format(date)
	}

	private DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd", request.locale)
	}
}
