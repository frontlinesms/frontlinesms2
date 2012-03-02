package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ImportControllerSpec extends ControllerSpec {
    /* works in grails 2.0 and above
	def "should import file a specified location"() {
		final file = new GrailsMockMultipartFile("csvFile.csv", "foo".bytes)
		request.addFile(file)
		controller.uploadCSVFile()
		println file.targetFileLocation.path
		assert file.targetFileLocation.path == "${homeDir}importedcsvfiles/csvFile.csv"
	}	*/
}

