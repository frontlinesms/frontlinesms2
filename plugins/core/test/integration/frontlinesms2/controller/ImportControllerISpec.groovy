package frontlinesms2.controller

import frontlinesms2.*
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
			// FIXME are there any problems with modifying getFile like this?
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
	
	def 'Uploading a messages CSV file should create new messages and folder in the database'() {
		given:
			// mock the file
			def csvFileContent = '''"Message Type","Message Status","Message Date","Message Content","Sender Number","Recipient Number"
"Received","Received","2012-02-16 16:42:24","Message Received Msg1.","Safaricom","254704593656"
"Received","Received","2012-02-24 17:22:59","Message Received Msg2.","254705693656","254704593656"
"Sent","Failed","2012-03-09 12:48:42","Message Sent Msg1","* N/A *","0720330266"
"Sent","Sent","2012-03-07 12:19:41","Message Sent Msg2","* N/A *","144"
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
			controller.importMessages()
		then:
			// check that messages and folders were created
			Fmessage.list()*.text.sort() == ['Message Received Msg1.', 'Message Received Msg2.', 'Message Sent Msg1', 'Message Sent Msg2']
			Folder.list().name == ['messages from v1']
	}
}
