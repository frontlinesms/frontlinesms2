package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(ErrorController)
class ErrorControllerSpec extends Specification {
	def setup() {
		File.metaClass.zip = { output, filter=null -> }
	}

	def 'logs should be provided with MIME type for zip file'() {
		when:
			controller.logs()
		then:
			controller.response.contentType == 'application/x-zip-compressed'
	}

	def 'logs should be provided with suitable filename and download should be forced'() {
		when:
			controller.logs()
		then:
			println "methods: ${controller.response.class.metaClass.methods*.name.sort().unique()}"
			controller.response.getHeader('Content-disposition').value ==~ /attachment; filename=frontlinesms2-log-\d{4}-\d{2}-\d{2}\.zip/
	}

	def 'logs and db should be provided with MIME type for zip file'() {
		when:
			controller.logsAndDatabase()
		then:
			controller.response.contentType == 'application/x-zip-compressed'
	}

	def 'logs and db should be provided with suitable filename and download should be forced'() {
		when:
			controller.logsAndDatabase()
		then:
			controller.response.getHeader('Content-disposition').value ==~ /attachment; filename=frontlinesms2-log-and-database-\d{4}-\d{2}-\d{2}\.zip/
	}

	def 'createException should throw runtime exception'() {
		when:
			controller.createException()
		then:
			thrown(RuntimeException)
	}
}

