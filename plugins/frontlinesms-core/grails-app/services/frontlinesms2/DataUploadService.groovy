package frontlinesms2
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC

class DataUploadService {

	def upload(String url,Map dataToSend) {
		def http = new HTTPBuilder(url)
		http.post(body: dataToSend, requestContentType: URLENC ) { resp ->
			assert resp.statusLine.statusCode == 200
			println resp
		}
	}
}
