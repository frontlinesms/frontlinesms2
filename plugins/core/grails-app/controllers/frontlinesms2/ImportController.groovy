package frontlinesms2
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat

class ImportController {
	private static final def MESSAGE_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

	def importContacts = {
		def savedCount = 0
		def failedCount = 0
		def uploadedCSVFile = request.getFile('importCsvFile')
		
		if(uploadedCSVFile) {
			def headers
			def standardFields = ['Name':'name', 'Mobile Number':'primaryMobile',
					'E-mail Address':'email', 'Notes':'notes']
			uploadedCSVFile.inputStream.toCsvReader([escapeChar:'¬']).eachLine { tokens ->
				if(!headers) headers = tokens
				else try {
					Contact c = new Contact()
					def groups
					headers.eachWithIndex { key, i ->
						def value = tokens[i]
						if(key in standardFields) {
							c."${standardFields[key]}" = value
						} else if(key == 'Group(s)') {
							def groupNames = getGroupNames(value)
							println "Group names: $groupNames"
							groups = getGroups(groupNames)
						} else {
							new CustomField(name:key, value:value, contact:c)
						}
					}
					c.save(failOnError:true)
					if(groups) groups.each { c.addToGroup(it) }
					++savedCount
				} catch(Exception ex) {
					ex.printStackTrace() // TODO replace this with logging
					++failedCount
				}
			}
			flash.message = "$savedCount contacts were imported; $failedCount failed" 
			redirect controller: "settings", action: 'general'
		} else throw new RuntimeException("File upload has failed for some reason.")
	}
	
	def importMessages = {
		def savedCount = 0
		def failedCount = 0
		def uploadedCSVFile = request.getFile('importCsvFile')
		if(uploadedCSVFile) {
			def headers
			def standardFields = ['Message Type':'inbound', 'Message Status':'hasFailed',
					'Message Content':'text', 'Sender Number':'src']
			uploadedCSVFile.inputStream.toCsvReader([escapeChar:'¬']).eachLine { tokens ->
				if(!headers) headers = tokens 
				else try {
					Fmessage fm = new Fmessage()
					headers.eachWithIndex { key, i ->
						def value = tokens[i]
						if (key in standardFields) {
							fm[standardFields[key]] = value
						} else if (key == 'Message Date') {
							fm.date = MESSAGE_DATE.parse(value)
						} else if (key == 'Recipient Number') {
							fm.addToDispatches(getDispatch(value))
						}
					}
					validateConstraints(fm)
					fm.save(failOnError:true)
					++savedCount
					getMessageFolder("messages from v1").addToMessages(fm)
				} catch(Exception ex) {
					ex.printStackTrace() // TODO replace this with logging
					++failedCount
				}
			}
		}
	}
	
	Fmessage validateConstraints(fm) {
		if (fm.inbound == 'Received') {
			fm.inbound = true
			fm.hasFailed = false
			fm.dispatches = []
		} else {
			fm.inbound = false
			fm.hasFailed == 'Failed'||'Pending'||'Outbox'?true: false
		}
		return fm
	}
	
	def getMessageFolder(name) {
		Folder.findByName(name)?: new Folder(name: name).save(failOnError:true)
	}
	
	def getDispatch(csvValue) {
		new Dispatch(dst:csvValue, status:DispatchStatus.FAILED)
	}
	
	def getGroupNames(csvValue) {
		println "getGroupNames() : csvValue=$csvValue"
		Set csvGroups = []
		csvValue.split("\\\\").each { gName ->
			def longName
			gName.split("/").each { shortName ->
				csvGroups << shortName
				longName = longName? "$longName-$shortName": shortName
				csvGroups << longName
			}
		}
		println "getGroupNames() : ${csvGroups - ''}"
		return csvGroups - ''
	}
	
	def getGroups(groupNames) {
		println "ImportController.getGroups() : $groupNames"
		groupNames.collect { name ->
			Group.findByName(name)?: new Group(name:name).save(failOnError:true)
		}
	}
}