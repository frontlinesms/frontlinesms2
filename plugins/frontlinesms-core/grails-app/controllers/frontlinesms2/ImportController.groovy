package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.io.StringWriter

import au.com.bytecode.opencsv.CSVWriter
import au.com.bytecode.opencsv.CSVParser
import ezvcard.*

class ImportController extends ControllerUtils {
	private final MESSAGE_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	private final STANDARD_FIELDS = ['Name':'name', 'Mobile Number':'mobile',
					'Email':'email', 'Group(s)':'groups', 'Notes':'notes']
	private final CONTENT_TYPES = [csv:'text/csv', vcf:'text/vcard', vcfDepricated:'text/directory']

	def contactImportService
	def systemNotificationService

	def importData() {
		log.info "ImportController.importData() :: params=$params"
		if(params.data == 'messages') {
			importMessages()
		} else {
			importContacts()
		}
	}

	private def importContacts() {
		log.info "ImportController.importContacts() :: ENTRY"
		if(params.reviewDone) {
			ImportContactsJob.triggerNow(['fileType':'csv', 'params':params, 'request':request])
			systemNotificationService.create(code:'importing.status.label', topic:'import.status')
			redirect controller:'contact', action:'show' 
			return
		}
		switch(request.getFile('importCsvFile').contentType) {
			case [CONTENT_TYPES.vcf, CONTENT_TYPES.vcfDepricated]:
				ImportContactsJob.triggerNow(['fileType':'vcf', 'params':params, 'request':request])
				systemNotificationService.create(code:'importing.status.label', topic:'import.status')
				redirect controller:'contact', action:'show' 
				break
			default:
				prepareCsvReview()
		}
		println "ImportController.importContact() :: EXIT"
	}

	private def prepareCsvReview() {
		log.info "ImportController.prepareCsvReview() :: ENTRY"
		def uploadedCSVFile = request.getFile('importCsvFile')
		def csvAsNestedLists = []
		def headerRowSize
		uploadedCSVFile.inputStream.toCsvReader([escapeChar:'�']).eachLine { tokens ->
			if(!headerRowSize) {
				headerRowSize = tokens.size()
			}
			if(tokens.size() == headerRowSize && tokens.find({it as Boolean})) {
				csvAsNestedLists << tokens
			}
		}
		session.csvData = csvAsNestedLists
		redirect action:'reviewContacts'
		return
	}

	private def importMessages() {
		def savedCount = 0
		def failedCount = 0
		def importingVersionOne = true
		def uploadedCSVFile = request.getFile('importCsvFile')
		if(uploadedCSVFile) {
			def headers
			def standardFields = ['Message Content':'text', 'Sender Number':'src']
			def dispatchStatuses = [Failed:DispatchStatus.FAILED,
					Pending:DispatchStatus.PENDING,
					Outbox:DispatchStatus.SENT,
					Sent:DispatchStatus.SENT]
			uploadedCSVFile.inputStream.toCsvReader([escapeChar:'�']).eachLine { tokens ->
				if(!headers) {
					headers = tokens
					// strip BOM from first value
					if(headers[0] && headers[0][0] == '\uFEFF') {
						headers[0] = headers[0].substring(1)
					}
				} else try {
					TextMessage fm = new TextMessage()
					def dispatchStatus
					headers.eachWithIndex { key, i ->
						def value = tokens[i]
						if (key in standardFields) {
							fm[standardFields[key]] = value
						} else if (key == 'Message Date') {
							fm.date = MESSAGE_DATE.parse(value)
						} else if (key == 'Recipient Number') {
							fm.addToDispatches(new Dispatch(dst:value))
						} else if(key == 'Message Type') {
							fm.inbound = (value == 'Received')
						} else if(key == 'Message Status') {
							dispatchStatus = dispatchStatuses[value]
						} else if (key == 'Source Mobile') { //version 2 import
							fm.src = value
							fm.inbound = true
							importingVersionOne = false
						} else if (key == 'Destination Mobile') {
							value = value.replace("[","")
							value.replace("]","").split(",").each{
								fm.addToDispatches(new Dispatch(dst:it))
							}
						} else if (key == 'Date Created') {
							fm.date = MESSAGE_DATE.parse(value)
						} else if (key == 'Text') {
							fm.text = value
						}
					}
					if (fm.inbound) fm.dispatches = []
					else fm.dispatches.each {
						it.status = dispatchStatus?: DispatchStatus.FAILED
						if (dispatchStatus==DispatchStatus.SENT) it.dateSent = fm.date
					}

					TextMessage.withNewSession {
						fm.save(failOnError:true)
					}
					++savedCount
					importingVersionOne ? saveMessagesIntoFolder("v1", fm) : saveMessagesIntoFolder("v2", fm)
				} catch(Exception ex) {
					ex.printStackTrace()
					log.info message(code:'import.message.save.error'), ex
					++failedCount
				}
			}
			flash.message = message(code: 'import.message.complete', args:[savedCount, failedCount])
			redirect controller:'settings', action:'porting'
		}
	}

	def failedContacts() {
		response.setHeader("Content-disposition", "attachment; filename=failedContacts.${params.format}")
		response.setHeader 'Content-Type', CONTENT_TYPES[params.format]
		def failedContactFileContent = contactImportService.getFailedContactsByKey(params.key)
		response.outputStream << failedContactFileContent
		response.outputStream.flush()
	}

	def reviewContacts() {
		if(!session.csvData) {
			redirect controller:'settings', action:'porting'
			return
		}
		[csvData:session.csvData, recognisedTitles:STANDARD_FIELDS.keySet()]
	}

	def contactWizard() {
		render(template:"/contact/import_contacts")
	}
}

