package frontlinesms2

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.text.SimpleDateFormat

import org.apache.jasper.compiler.Node.ParamsAction;

import grails.util.GrailsConfig

class ExportController {
	def exportService
	
	def beforeInterceptor = {
		params.viewingArchive = params.viewingArchive ? params.viewingArchive.toBoolean() : false
		params.starred = params.starred ? params.starred.toBoolean() : false
		params.failed = params.failed ? params.failed.toBoolean() : false
		true
	}
	
    def index = { redirect(action:'wizard', params: params) }
	
	def messageWizard = {
		[messageSection: params.messageSection,
				searchId: params.searchId,
				ownerId: params.ownerId,
				starred: params.starred,
				failed: params.failed,
				viewingArchive: params.viewingArchive,
				reportName:getActivityDescription()]
	}
	
	def downloadMessageReport = {
		def messageSection = params.messageSection
		def messageInstanceList
		switch(messageSection) {
			case 'inbox':
				messageInstanceList = Fmessage.inbox(params.starred, params.viewingArchive).list()
				break
			case 'sent':
				messageInstanceList = Fmessage.sent(params.starred, params.viewingArchive).list()
				break
			case 'pending':
				messageInstanceList = Fmessage.pending(params.failed).list()
				break
			case 'trash':
				messageInstanceList = Fmessage.trash().list()
				break
			case 'poll':
				messageInstanceList = Poll.get(params.ownerId).getActivityMessages(params.starred).list()
				break
			case 'folder':
				messageInstanceList = Folder.get(params.ownerId).getFolderMessages(params.starred).list()
				break
			case 'result':
				messageInstanceList = Fmessage.search(Search.get(params.searchId)).list()
				break
			default:
				messageInstanceList = Fmessage.findAll()
				break
		}
		generateMessageReport(messageInstanceList)
	}
	
	def contactWizard = {
		[groupId: params.groupId, 
			contactsSection: params.contactsSection,
			reportName:getGroupDescription()]
	}
	
	def downloadContactReport = {
		def groupId = params.groupId
		def contactInstanceList
		if(!params.groupId)
			contactInstanceList = Contact.getAll()
		else if(params.contactsSection == 'group')
			contactInstanceList = Group.get(params.groupId).getMembers()
		else if(params.contactsSection == 'smartGroup')
			contactInstanceList = SmartGroup.get(params.groupId).getMembers()
		generateContactReport(contactInstanceList)
	}
	
	private def generateMessageReport(messageInstanceList) {
		def currentTime = new Date()
		def formatedTime = dateToString(currentTime)
		List fields = ["id", "src", "dst", "text", "date"]
		Map labels = ["id":"DatabaseID", "src":"Source", "dst":"Destination", "text":"Text", "date":"Date Created"]
		Map parameters = [title: "FrontlineSMS Message Export"]
		response.setHeader("Content-disposition", "attachment; filename=FrontlineSMS_Message_Export_${formatedTime}.${params.format}")
		try{
			exportService.export(params.format, response.outputStream, messageInstanceList, fields, labels, [:],parameters)
		} catch(Exception e){
			render(text: "Error creating report")
		}
		[messageInstanceList: messageInstanceList]
	}
	
	private def generateContactReport(contactInstanceList) {
		def currentTime = new Date()
		def formatedTime = dateToString(currentTime)
		List fields = ["id", "name", "primaryMobile", "secondaryMobile", "email", "notes"]
		Map labels = ["id":"DatabaseID", "name":"Name", "primaryMobile":"Primary Mobile", "secondaryMobile":"Secondary Mobile", "email":"Email", "notes":"Notes"]
		Map parameters = [title: "FrontlineSMS Contact Export"]
		response.setHeader("Content-disposition", "attachment; filename=FrontlineSMS_Contact_Export_${formatedTime}.${params.format}")
		try{
			exportService.export(params.format, response.outputStream, contactInstanceList, fields, labels, [:],parameters)
		} catch(Exception e){
			render(text: "Error creating report")
		}
		[contactInstanceList: contactInstanceList]
	}
	
	private def getActivityDescription() {
		if(params.ownerId){
			String name
		 	switch(params.messageSection) {
				case 'poll':
					def poll = Poll.findById(params.ownerId)
					name = "${poll.title} poll (${poll.getActivityMessages(false).count()} messages)"
					break
				case 'folder':
					def folder = Folder.findById(params.ownerId)
					name = "${folder.name} folder (${folder.getFolderMessages(false).count()} messages)"
					break
			}
		} else {
			String name = "${params.messageSection} (${params.messageTotal} messages)"
		}
	}
	
	private def getGroupDescription() {
		if(params.groupId){
			String name
			 switch(params.contactsSection) {
				case 'group':
					def group = Group.findById(params.groupId)
					name = "${group.name} group (${group.getMembers().count()} contacts)"
					break
				case 'smartGroup':
					def smartGroup = SmartGroup.findById(params.groupId)
					name = "${smartGroup.name} smart group (${smartGroup.getMembers().count()} contacts)"
					break
			}
		} else {
			String name = "All contacts (${params.contactTotal} contacts)"
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
