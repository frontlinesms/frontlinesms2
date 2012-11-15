package frontlinesms2

import java.text.DateFormat;
import java.text.SimpleDateFormat

class ExportController {
	def exportService
	
	def beforeInterceptor = {
		params.viewingArchive = params.viewingArchive ? params.viewingArchive.toBoolean() : false
		params.starred = params.starred ? params.starred.toBoolean() : false
		params.inbound = params.inbound ? params.inbound.toBoolean() : null
		params.failed = params.failed ? params.failed.toBoolean() : false
		true
	}
	
	def index() { redirect(action:'wizard', params: params) }
	
	def messageWizard() {
		[messageSection: params.messageSection,
				searchId: params.searchId,
				ownerId: params.ownerId,
				starred: params.starred,
				inbound: params.inbound,
				failed: params.failed,
				viewingArchive: params.viewingArchive,
				reportName:getActivityDescription()]
	}
	
	def downloadMessageReport() {
		def messageSection = params.messageSection
		def messageInstanceList
		//TODO Clean up switch mess
		switch(messageSection) {
			case 'inbox':
				messageInstanceList = Fmessage.inbox(params.starred, params.viewingArchive).list()
				break
			case 'sent':
				messageInstanceList = Fmessage.sent(params.starred, params.viewingArchive).list()
				break
			case 'pending':
				messageInstanceList = Fmessage.listPending(params.failed?:false, [:])
				break
			case 'trash':
				messageInstanceList = Fmessage.trash().list()
				break
			case 'activity':
				messageInstanceList = Activity.get(params.ownerId).getActivityMessages(params.starred?:false, params.inbound).list()
				break
			case 'folder':
				messageInstanceList = Folder.get(params.ownerId).getFolderMessages(params.starred?:false, params.inbound).list()
				break
			case 'radioShow':
				messageInstanceList = MessageOwner.get(params.ownerId).getShowMessages().list()
				break
			case 'result':
				messageInstanceList = Fmessage.search(Search.get(params.searchId)).list()
				break
			default:
				messageInstanceList = Fmessage.findAll()
				break
		}
		
		generateMessageReport(messageInstanceList.unique())
	}
	
	def contactWizard() {
		[groupId: params.groupId, 
			contactsSection: params.contactsSection,
			reportName:getGroupDescription()]
	}
	
	def downloadContactReport() {
		def contactInstanceList
		if(!params.groupId) {
			contactInstanceList = Contact.getAll()
		} else if(params.contactsSection == 'group') {
			contactInstanceList = Group.get(params.groupId).getMembers()
		} else if(params.contactsSection == 'smartgroup') {
			contactInstanceList = SmartGroup.get(params.groupId).getMembers()
		} else {
			throw new RuntimeException("Unrecognised section: $params.contactsSection")
		}
		generateContactReport(contactInstanceList)
	}

	private def generateMessageReport(messageInstanceList) {
		def currentTime = new Date()
		def formatedTime = dateToString(currentTime)
		List fields = ["id", "inboundContactName", "src", "outboundContactList", "dispatches.dst", "text", "date"]
		Map labels = ["id":message(code: 'export.database.id'), "inboundContactName":message(code: 'export.message.source.name'),"src":message(code: 'export.message.source.mobile'), "outboundContactList":message(code: 'export.message.destination.name'), "dispatches.dst":message(code: 'export.message.destination.mobile'), "text":message(code: 'export.message.text'), "date":message(code: 'export.message.date.created')]
		Map parameters = [title: message(code: 'export.message.title')]
		response.setHeader("Content-disposition", "attachment; filename=FrontlineSMS_Message_Export_${formatedTime}.${params.format}")
		try{
			exportService.export(params.format, response.outputStream, messageInstanceList, fields, labels, [:], parameters)
		} catch(Exception e){
			render(text: message(code: 'report.creation.error'))
		}
		[messageInstanceList: messageInstanceList]
	}
	
	private def generateContactReport(contactInstanceList) {
		def currentTime = new Date()
		def formatedTime = dateToString(currentTime)
		List fields = ["name", "mobile", "email", "notes", "groupMembership"]
		Map labels = params.format == "csv" ? 
			["name":"Name", "mobile":"Mobile Number", "email":"E-mail Address", "notes":"Notes", "groupMembership":"Group(s)"]
			: ["name":message(code: 'export.contact.name'), "mobile":message(code: 'export.contact.mobile'), "email":message(code: 'export.contact.email'), "notes":message(code: 'export.contact.notes'), "groupMembership":message(code: 'export.contact.groups')]
		// add custom fields
		def customFields = CustomField.getAllUniquelyNamed()
		customFields.each { field ->
			fields << field
			labels << ["{$field}":field]
			contactInstanceList.each { contact ->
				contact.metaClass."${field}" = (contact.customFields.find { it.name == field})?.value
			}
		}
		// add groups
		contactInstanceList.each { contact ->
			contact.metaClass.groupMembership = contact.groups*.name.join("\\\\")
		}
		Map parameters = [title: message(code: 'export.contact.title')]
		response.setHeader("Content-disposition", "attachment; filename=FrontlineSMS_Contact_Export_${formatedTime}.${params.format}")
		try{
			exportService.export(params.format, response.outputStream, contactInstanceList, fields, labels, [:],parameters)
		} catch(Exception e){
			render(text: message(code: 'report.creation.error'))
		}
		[contactInstanceList: contactInstanceList]
	}
	
	private def getActivityDescription() {
		if(params.ownerId){
			def messageOwner = MessageOwner.findById(params.ownerId)
			return message(code: 'export.messages.name1', args: [messageOwner.name, messageOwner.shortName, params.messageTotal])
		} else {
			return message(code: 'export.messages.name2', args: [params.messageSection, params.messageTotal])
		}
	}

	private def getGroupDescription() {
		if(params.groupId){
			switch(params.contactsSection) {
				case 'group':
					def group = Group.findById(params.groupId)
					return message(code: 'export.contacts.name1', args: [group.name, group.getMembers().size()])
				case 'smartGroup':
					def smartGroup = SmartGroup.findById(params.groupId)
					return message(code: 'export.contacts.name2', args: [smartGroup.name, smartGroup.getMembers().size()])
			}
		} else {
			return message(code: 'export.contacts.name3', args: [params.contactTotal])
		}
	}

	private String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat()
		return formatedDate.format(date)
	}

	private DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyyMMdd", request.locale)
	}
}
