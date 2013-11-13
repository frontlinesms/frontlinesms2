package frontlinesms2.controller

import frontlinesms2.*
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile

class ImportControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new ImportController()
	}

	def 'Uploading a contacts CSV file should create new contacts and groups in the database'() {
		when:
			importContacts('''"Name","Mobile Number","Other Mobile Number","E-mail Address","Current Status","Notes","Group(s)"
"Alice Sihoho","+254728749000","","","true","","/ToDo/Work"
"Amira Cheserem","+254715840801","","","true","","/ToDo/Work"
"anyango Gitu","+254727689908","","","true","","/isIt\\\\/ToDo/Work/jobo"
''')
		then:
			// check that contacts and groups were created
			Contact.list()*.name.sort() == ['Alice Sihoho', 'Amira Cheserem', 'anyango Gitu']
			Group.list()*.name.sort() == ['ToDo', 'ToDo-Work', 'ToDo-Work-jobo', 'Work', 'isIt', 'jobo']
	}
	
	def 'Uploading a contacts CSV file with failed contacts should create failed contacts in a file'() {
		given:
			importContacts('''"Name","Mobile Number","Other Mobile Number","E-mail Address","Current Status","Notes","Group(s)"
"Alice Sihoho254728749000","","","true","","/ToDo/Work"
"Amira Cheserem","+254715840801","","","true","","/ToDo/Work"
"anyango Gitu","+254727689908","","","true","","/isIt\\/ToDo/Work/jobo"
''')
		when:
			// failed contacts file is downloaded
			controller.params.failedContacts = controller.flash.failedContacts
			controller.failedContacts()
		then:
			// check that headers are correctly set
			controller.response.getHeader('Content-disposition') == 'attachment; filename=failedContacts.csv'
			// check that body is correcty set
			controller.response.contentAsString == '''"Name","Mobile Number","Other Mobile Number","E-mail Address","Current Status","Notes","Group(s)"
"Alice Sihoho254728749000","","","true","","/ToDo/Work"
'''
	}
	def 'uploading contacts with backslash characters should unfortunately interpret them as separate groups'() {
		when:
			importContacts('''"Name","Mobile Number","E-mail Address","Notes","Group(s)","lake","town"
"Alex","0702597711",,,"\\o/ team",,
"Enock","0711756950",,,"\\o/ team",,
"Geoff","0725675317",,,"\\o/ team",,
"Vaneyck","0723127992",,,"\\o/ team",,
''')
		then:
			Group.list()*.name == ['o', 'o- team', 'team']
			Contact.count() == 4
			def groups = Group.findAll()
			Contact.findAll().every { groups.every { group -> it.isMemberOf(group) } }
	}
	
	def 'Uploading a messages CSV file from version 1 should create new messages and folder in the database'() {
		when:
			importMessages('''"Message Type","Message Status","Message Date","Message Content","Sender Number","Recipient Number"
"Received","Received","2012-02-16 16:42:24","Message Received Msg1.","Safaricom","254704593656"
"Received","Received","2012-02-24 17:22:59","Message Received Msg2.","254705693656","254704593656"
"Sent","Failed","2012-03-09 12:48:42","Message Sent Msg1","* N/A *","0720330266"
"Sent","Sent","2012-03-07 12:19:41","Message Sent Msg2","* N/A *","144"
''')
		then:
			// check that messages and folders were created
			Fmessage.list()*.text.sort() == ['Message Received Msg1.', 'Message Received Msg2.', 'Message Sent Msg1', 'Message Sent Msg2']
			Folder.list().name == ['messages from v1']
			Fmessage.list()*.messageOwner.name.every { it == 'messages from v1' }
	}
	
	def 'Uploading a messages CSV file from version 2 should create new messages and folder in the database'() {
		when:
			importMessages('''"DatabaseID","Source Name","Source Mobile","Destination Name","Destination Mobile","Text","Date Created"
"302","Simon","+123987123","","[]","Message 1","2012-07-27 10:22:02.943"
"302","Says","+123987123","","[]","Message 2","2012-07-27 10:22:02.943"
"302","Import","+123987123","","[]","Message 3","2012-07-27 10:22:02.943"
''')
		then:
			// check that messages and folders were created
			Fmessage.list()*.text.sort() == ['Message 1', 'Message 2', 'Message 3']
			Folder.list().name == ['messages from v2']
			Fmessage.list()*.messageOwner.name.every { it == 'messages from v2' }
	}

	def 'Uploading a messages CSV file from version 2 should be able to handle line breaks in messages'() {
		when:
			importMessages('''"DatabaseID","Source Name","Source Mobile","Destination Name","Destination Mobile","Text","Date Created"
"27",,+12345678,"[Bobby Briggs]","[+2547123456]","Joyce
Vancouver
Siloi
Rotation
Amelia
Georgina
Shantelle","2012-06-12 15:58:44.488"
''')
		then:
			// check that messages and folders were created
			Fmessage.list()*.text.sort() == ['''Joyce
Vancouver
Siloi
Rotation
Amelia
Georgina
Shantelle''']
			Folder.list().name == ['messages from v2']
			Fmessage.list()*.messageOwner.name.every { it == 'messages from v2' }
	}

	def 'Uploading a message with a very long content field results in the message content being...'() {
		when:
			importMessages('''"Message Type","Message Status","Message Date","Message Content","Sender Number","Recipient Number"
"Received","Received","2012-02-16 16:42:24","''' + ('0123456789ABCDEF' * 256)  + '''","Safaricom","254704593656"
"Received","Received","2012-02-24 17:22:59","short message","254705693656","254704593656"
''')
		then:
			Fmessage.list()*.text == ['short message', ('0123456789ABCDEF' * 256)[0..1598] + '…']
	}

	def 'Uploading a CSV with a BOM should not cause issues'() {
		when:
			importMessages('''"\uFEFFMessage Type","Message Status","Message Date","Message Content","Sender Number","Recipient Number"
"Received","Received","2012-02-16 16:42:24","+123456789","Safaricom","254704593656"
''')
		then:
			Fmessage.list()*.inbound == [true]
	}

	def importMessages(String fileContent) {
		mockFileUpload('importCsvFile', fileContent)
		controller.importMessages()
	}

	def importContacts(String csvContent) {
		controller.params.csv = csvContent
		controller.importContacts()
	}

	def mockFileUpload(filename, fileContent) {
		controller.request.addFile(new GrailsMockMultipartFile(filename, fileContent.getBytes("UTF-8")))
	}
}

