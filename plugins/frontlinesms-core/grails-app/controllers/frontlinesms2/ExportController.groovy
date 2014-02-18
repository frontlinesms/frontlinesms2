package frontlinesms2

import java.text.SimpleDateFormat
import ezvcard.*

class ExportController extends ControllerUtils {
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
		def interactionInstanceList
		//TODO Clean up switch mess
		switch(messageSection) {
			case 'inbox':
				interactionInstanceList = TextMessage.inbox(params.starred, params.viewingArchive).list()
				break
			case 'sent':
				interactionInstanceList = TextMessage.sent(params.starred, params.viewingArchive).list()
				break
			case 'pending':
				interactionInstanceList = TextMessage.listPending(params.failed, params)
				break
			case 'trash':
				interactionInstanceList = TextMessage.trash().list()
				break
			case 'activity':
				interactionInstanceList = Activity.get(params.ownerId).getActivityMessages(params.starred?:false, params.inbound)
				break
			case 'folder':
				interactionInstanceList = Folder.get(params.ownerId).getFolderMessages(params.starred?:false, params.inbound).list()
				break
			case 'radioShow':
				interactionInstanceList = MessageOwner.get(params.ownerId).getShowMessages().list()
				break
			case 'result':
				interactionInstanceList = TextMessage.search(Search.get(params.searchId)).list()
				break
			default:
				interactionInstanceList = TextMessage.findAll()
				break
		}
		
		generateMessageReport(interactionInstanceList.unique())
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
		if(params.format == 'vcf') {
			exportContactVcf(contactInstanceList)
		} else {
			generateContactReport(contactInstanceList)
		}
	}

	private def exportContactVcf(contactInstanceList) {
		response.setHeader 'Content-disposition',
				"attachment; filename=FrontlineSMS_Contact_Export_${formatedTime}.${params.format}"
		response.setHeader 'Content-Type', 'text/vcard'
		def cards = contactInstanceList.collect { c ->
			def v = new VCard()
			v.setFormattedName(c.name)
			v.addTelephoneNumber(c.mobile)
			v.addEmail(c.email)
			return v
		}
		render text:Ezvcard.write(cards).go()
	}

	private def generateMessageReport(interactionInstanceList) {
		List fields = ["id", "inboundContactName", "src", "outboundContactList", "dispatches.dst", "text", "date"]
		Map labels = ["id":message(code: 'export.database.id'), "inboundContactName":message(code: 'export.message.source.name'),"src":message(code: 'export.message.source.mobile'), "outboundContactList":message(code: 'export.message.destination.name'), "dispatches.dst":message(code: 'export.message.destination.mobile'), "text":message(code: 'export.message.text'), "date":message(code: 'export.message.date.created')]
		Map parameters = [title: message(code: 'export.message.title')]
		setUnicodeParameter(parameters)
		response.setHeader("Content-disposition", "attachment; filename=FrontlineSMS_Message_Export_${formatedTime}.${params.format}")
		try {
			exportService.export(params.format, response.outputStream, interactionInstanceList, fields, labels, [:], parameters)
		} catch(Exception e) {
			render(text: message(code: 'report.creation.error'))
		}
		[interactionInstanceList: interactionInstanceList]
	}
	
	private def generateContactReport(contactInstanceList) {
		List fields = ["name", "mobile", "email", "notes", "groupMembership"]
		Map labels = params.format == "csv" ? 
			["name":"Name", "mobile":"Mobile Number", "email":"Email", "notes":"Notes", "groupMembership":"Group(s)"]
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
		setUnicodeParameter(parameters)
		response.setHeader("Content-disposition", "attachment; filename=FrontlineSMS_Contact_Export_${formatedTime}.${params.format}")
		try {
			exportService.export(params.format, response.outputStream, contactInstanceList, fields, labels, [:],parameters)
		} catch(Exception e) {
			render(text: message(code: 'report.creation.error'))
		}
		[contactInstanceList: contactInstanceList]
	}
	
	private setUnicodeParameter(parameters) {
		if(params.format == 'pdf') {
			parameters << ["pdf.encoding":"UniGB-UCS2-H", "font.family": "STSong-Light"]
		} else if(params.format == "csv"){
			parameters << ['encoding':'UTF-8']
		}
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

	private String getFormatedTime() {
		new SimpleDateFormat("yyyyMMdd", request.locale).format(new Date())
	}
}

