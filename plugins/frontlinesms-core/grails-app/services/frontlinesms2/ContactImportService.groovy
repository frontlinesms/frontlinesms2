package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.io.StringWriter
import java.security.MessageDigest

import au.com.bytecode.opencsv.CSVWriter
import au.com.bytecode.opencsv.CSVParser
import ezvcard.*

class ContactImportService {
	private final STANDARD_FIELDS = ['Name':'name', 'Mobile Number':'mobile',
					'Email':'email', 'Group(s)':'groups', 'Notes':'notes']

	def systemNotificationService
	def i18nUtilService
	def grailsLinkGenerator
	def failedContactList = []
	def sessionFactory

	def synchronized saveFailedContacts(failedContactInstance) {
		failedContactList << failedContactInstance
	}

	def getFailedContactsByKey(k) {
		def failedContactInstance = failedContactList.find { it.key == k }
		def fileContents = failedContactInstance?.fileContent?:''
		def systemNotificationTopic = "failed.contact.${failedContactInstance?.key}"
		failedContactList.remove(failedContactInstance)
		def failedImportNotification = SystemNotification.findByTopic(systemNotificationTopic)
		failedImportNotification?.read = true
		failedImportNotification?.save()
		fileContents
	}

	def importContactCsv(params, request) {
		log.info "ContactImportService.importContactCsv) :: ENTRY"
		def savedCount = 0
		def processedCount = 0
		def headers
		def parser = new CSVParser()
		def failedLines = []
		params.csv.eachLine { line ->
			if(processedCount % 100 == 0) {
				cleanUpGorm()
			}
			def tokens = parser.parseLine(line)
			if(!headers) headers = tokens
			else try {
				if(headers.any { it.size() == 0 }) {
					throw new RuntimeException("Empty headers in some contact import columns")
				}
				Contact c = new Contact()
				def groups
				def customFields = []
				headers.eachWithIndex { key, i ->
					def value = tokens[i]
					if(key in STANDARD_FIELDS && key != 'Group(s)') {
						c."${STANDARD_FIELDS[key]}" = value
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
				log.info i18nUtilService.getMessage(code: 'import.contact.save.error'), ex
				log.info "ContactImportService.importContactsCsv :: exception :: $ex"
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

		if(savedCount > 0 && !failedLines) {
			systemNotificationService.create(code:'import.contact.complete', args:[savedCount])
		} else {
			def failedContactInstance = new FailedContact('csv', failedLineWriter.toString())
			saveFailedContacts(failedContactInstance)
			def downloadLink = grailsLinkGenerator.link(controller:'import', action:'failedContacts', params:[format:'csv', key:failedContactInstance.key])
			def aTag = "<a href='$downloadLink'>${i18nUtilService.getMessage(code:'download.label')}</a>"
			systemNotificationService.create(code:'import.contact.failed.info', topic:"failed.contact.${failedContactInstance.key}", args:[savedCount, failedLines.size(), aTag])
		}
		SystemNotification.findByTopic('import.status')?.delete()
	}

	def importContactVcard(params, request) {
		log.info "ContactImportService.importContactVcard() :: ENTRY"
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
				Contact.withNewSession {
					new Contact(name:v.formattedName.value, mobile:mobile, email:email).save(failOnError:true)
				}
				++savedCount
			} catch(Exception ex) {
				failedVcards << v
			}
		}
		def parse = { format='', exceptionClass=null ->
			try {
				Ezvcard."parse${format.capitalize()}"(uploadFile.inputStream)
						.all()
						.eachWithIndex { it, index ->
							processCard it
							if (index % 100 == 0) {
								cleanUpGorm()
							}
						}
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
			systemNotificationService.create(code:'import.contact.failed.invalid.vcf.file')
			throw new RuntimeException('Failed to parse vcf.')
		}

		if(savedCount > 0 && !failedVcards) {
			systemNotificationService.create(code:'import.contact.complete', args:[savedCount])
		} else {
			def failedContactInstance = new FailedContact('vcf', Ezvcard.write(failedVcards).go())
			saveFailedContacts(failedContactInstance)
			def downloadLink = grailsLinkGenerator.link(controller:'import', action:'failedContacts', params:[format:'vcf', key:failedContactInstance.key])
			def aTag = "<a href='$downloadLink'>${i18nUtilService.getMessage(code:'download.label')}</a>"
			systemNotificationService.create(code:'import.contact.failed.info', topic:"failed.contact.${failedContactInstance.key}",args:[savedCount, failedVcards.size(), aTag])
		}
		SystemNotification.findByTopic('import.status')?.delete()
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

	private def cleanUpGorm() {
		def session = sessionFactory.currentSession
		session.flush()
		session.clear()
	}
}

class FailedContact {
	String key
	String fileType
	String fileContent

	FailedContact(fileType, fileContent) {
		def dataToHash = new Date().toString() + fileContent
		this.key =  generateMD5(dataToHash.toString())
		this.fileType =  fileType
		this.fileContent = fileContent
	}

	def generateMD5(String s) {
		MessageDigest digest = MessageDigest.getInstance("MD5")
		digest.update(s.bytes);
		new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
	}
}
