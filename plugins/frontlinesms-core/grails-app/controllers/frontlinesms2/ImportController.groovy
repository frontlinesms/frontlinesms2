package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.io.StringWriter

import au.com.bytecode.opencsv.CSVWriter
import ezvcard.*

class ImportController extends ControllerUtils {
	private final def MESSAGE_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	private final CONTENT_TYPES = [csv:'text/csv', vcf:'text/vcard']
	
	def importData() {
		if (params.data == 'contacts') importContacts()
		else importMessages()
	}

	private def importContacts() {
		switch(request.getFile('importCsvFile').contentType) {
			case CONTENT_TYPES.vcf:
				importContactVcard()
				break
			default:
				importContactCsv()
		}
	}

	private def importContactVcard() {
		def failedVcards = []
		def savedCount = 0
		def uploadFile = request.getFile('importCsvFile')
		def processCard = { v ->
			def mobile = v.telephoneNumbers? v.telephoneNumbers.first(): null
			if(mobile) {
				mobile = mobile.text?: mobile.uri?.number?: ''
				mobile = mobile.replaceAll(/[^+\d]/, '')
			}
			def email = v.emails? v.emails.first().value: ''
			try {
				new Contact(name:v.formattedName.value,
						mobile:mobile,
						email:email).save(failOnError:true)
				++savedCount
			} catch(Exception ex) {
				failedVcards << v
			}
		}
		def parse = { format='', exceptionClass=null ->
			try {
				Ezvcard."parse${format.capitalize()}"(uploadFile.inputStream)
						.all()
						.each processCard
			} catch(Exception ex) {
				if(exceptionClass && ex.class.isAssignableFrom(exceptionClass)) {
					return false
				}
				throw ex
			}
			return true
		}
		if(!(parse('xml', org.xml.sax.SAXParseException) ||
				parse('json', com.fasterxml.jackson.core.JsonParseException) ||
				(parse('html') && parse()))) {
			throw new RuntimeException('Failed to parse vcf.')
		}
		if(savedCount > 0) {
			flash.message = g.message(code:'import.contact.complete', args:[savedCount])
		}
		flash.failedContacts = Ezvcard.write(failedVcards).go()
		flash.numberOfFailedLines = failedVcards.size()
		flash.failedContactsFormat = 'vcf'
		redirect controller:'settings', action:'porting'
	}
	
	private def importContactCsv() {
		def savedCount = 0
		def uploadedCSVFile = request.getFile('importCsvFile')
		
		if(uploadedCSVFile) {
			def headers
			def failedLines = []
			def standardFields = ['Name':'name', 'Mobile Number':'mobile',
					'E-mail Address':'email', 'Notes':'notes']
			uploadedCSVFile.inputStream.toCsvReader([escapeChar:'�']).eachLine { tokens ->
				if(!headers) headers = tokens
				else try {
					Contact c = new Contact()
					def groups
					def customFields = []
					headers.eachWithIndex { key, i ->
						def value = tokens[i]
						if(key in standardFields) {
							c."${standardFields[key]}" = value
						} else if(key == 'Group(s)') {
							def groupNames = getGroupNames(value)
							groups = getGroups(groupNames)
						} else {
							if (value.size() > 0 ){
								customFields << new CustomField(name:key, value:value)
							}
						}
					}
					// TODO not sure why this has to be done in a new session, but grails
					// can't cope with failed saves if we don't do this
					Contact.withNewSession {
						c.save(failOnError:true)
						if(groups) groups.each { c.addToGroup(it) }
						if(customFields) customFields.each { c.addToCustomFields(it) }
						c.save()
					}
					++savedCount
				} catch(Exception ex) {
					log.info message(code: 'import.contact.save.error'), ex
					failedLines << tokens
				}		
			}

			def failedLineWriter = new StringWriter()
			if(failedLines) {
				def writer
				try {
					writer = new CSVWriter(failedLineWriter)
					writer.writeNext(headers)
					failedLines.each { writer.writeNext(it) }
				} finally { try { writer.close() } catch(Exception ex) {} }
			}

			if(savedCount > 0) {
				flash.message = g.message(code:'import.contact.complete', args:[savedCount])
			}
			flash.failedContacts = failedLineWriter.toString()
			flash.numberOfFailedLines = failedLines.size()
			flash.failedContactsFormat = 'csv'
			redirect controller:'settings', action:'porting'
		} else throw new RuntimeException(message(code:'import.upload.failed'))
	}

	def failedContacts() {
		response.setHeader("Content-disposition", "attachment; filename=failedContacts.${params.format}")
		response.setHeader 'Content-Type', CONTENT_TYPES[params.format]
		params.failedContacts.eachLine { response.outputStream << it << '\n' }
		response.outputStream.flush()
	}
	
	def importMessages() {
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
					Fmessage fm = new Fmessage()
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

					Fmessage.withNewSession {
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
			redirect controller:'settings', action:'general'
		}
	}
	
	private def getMessageFolder(name) {
		Folder.findByName(name)?: new Folder(name:name).save(failOnError:true)
	}

	private saveMessagesIntoFolder(version, fm){
		getMessageFolder("messages from "+version).addToMessages(fm)
	}

	private def getGroupNames(csvValue) {
		Set csvGroups = []
		csvValue.split("\\\\").each { gName ->
			def longName
			gName.split("/").each { shortName ->
				csvGroups << shortName
				longName = longName? "$longName-$shortName": shortName
				csvGroups << longName
			}
		}
		return csvGroups - ''
	}
	
	private def getGroups(groupNames) {
		groupNames.collect { name ->
			name = name.trim()
			Group.findByName(name)?: new Group(name:name).save(failOnError:true)
		}
	}

	private def getFailedContactsFile() {
		if(!params.jobId || params.jobId!=UUID.fromString(params.jobId).toString()) params.jobId = UUID.randomUUID().toString()
		def f = new File(ResourceUtils.resourcePath, "import_contacts_${params.jobId}.csv")
		f.deleteOnExit()
		return f
	}
}
