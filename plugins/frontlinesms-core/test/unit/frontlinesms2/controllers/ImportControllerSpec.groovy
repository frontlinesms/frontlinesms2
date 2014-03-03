package frontlinesms2.controllers

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*

@TestFor(ImportController)
@Mock([ContactImportService])
class ImportControllerSpec extends Specification {
	def 'failedContacts should trigger a download for a file named failedContacts.csv'() {
		given:
			controller.contactImportService =  Mock(ContactImportService)
			controller.params.failedContacts = '''\
			"Name","Mobile Number","Email","Notes","Group(s)","lake","town"
			"Alice","+123456789","","","Friends\\Not Cats","Victoria",""
			"Bob","+198765432","","","Friends\\Not Cats","","Kusumu"
			"Kate","+198730948","","","","",""
			"Bobby","987654321","","","Camping Group\\Football Updates","",""
			"Sam","987654322","","","Camping Group","",""
			"Ron","987654323","","","Football Updates","",""
			'''
			controller.params.format = "csv"
		when:
			controller.failedContacts()
		then:
			
			controller.response.getHeader('Content-disposition') == "attachment; filename=failedContacts.csv"
	}

	def 'if maximum size is exceeded, the user is notified'() {
		given:
			def notified = false
			controller.contactImportService =  Mock(ContactImportService)
			controller.systemNotificationService = [create: { Map args -> notified = true }]
			controller.request.exception = true
		when:
			controller.importData()
		then:
			notified
	}
}

