package frontlinesms2.controller

import frontlinesms2.*
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat

import org.springframework.web.multipart.commons.CommonsMultipartFile

class ImportControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new ImportController()
	}
	
	def 'Uploading a contacts CSV file should create new contacts and groups in the database'() {
		given:
			// mock the file
			def csvFileContent = '''"Name","Mobile Number","Other Mobile Number","E-mail Address","Current Status","Notes","Group(s)"
"Alice Sihoho","+254728749000","","","true","","/ToDo/Work"
"Amira Cheserem","+254715840801","","","true","","/ToDo/Work"
"anyango Gitu","+254727689908","","","true","","/isIt\\/ToDo/Work/jobo"
'''
			controller.request.metaClass.getFile = { String originalFileName ->
				println "getFile() : name:$originalFileName"
				assert originalFileName == 'importCsvFile'
				[
					empty:false,
					originalFilename:'mockedFile.csv',
					inputStream: new ByteArrayInputStream(csvFileContent.getBytes("UTF-8"))
				]
			}
		when:
			// file is uploaded
			controller.importContacts()
		then:
			// check that contacts and groups were created
			Contact.list()*.name.sort() == ['Alice Sihoho', 'Amira Cheserem', 'anyango Gitu']
			Group.list()*.name.sort() == ['ToDo', 'ToDo-Work', 'ToDo-Work-jobo', 'Work', 'isIt', 'jobo']
	}
}
