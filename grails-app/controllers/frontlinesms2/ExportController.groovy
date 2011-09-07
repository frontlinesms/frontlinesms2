package frontlinesms2

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.text.SimpleDateFormat

import org.apache.jasper.compiler.Node.ParamsAction;

import grails.util.GrailsConfig

class ExportController {
	def exportService
	
    def index = { redirect(action:'wizard', params: params) }
	
	def wizard = {
		println "params: $params"
		[messageInstanceList: params.messageList,
			messageSection: params.messageSection,
			ownerId: params.ownerId,
			activityId: params.activityId,
			searchString: params.searchString,
			groupId: params.groupId,
			reportName:getActivityDescription()
			]
	}
	
	def downloadReport = {
		def messageSection = params.messageSection
		println "section: ${messageSection}"
		println "wiz params: $params"
		def messageInstanceList
		switch(messageSection) {
			case 'inbox':
				messageInstanceList = Fmessage.inbox.list()
				break
			case 'sent':
				messageInstanceList = Fmessage.sent.list()
				break
			case 'pending':
				messageInstanceList = Fmessage.pending.list()
				break
			case 'trash':
				messageInstanceList = Fmessage.trash.list()
				break
			case 'poll':
				messageInstanceList = Poll.get(params.ownerId).getMessages(['starred':false])
				break
			case 'folder':
				messageInstanceList = Folder.get(params.ownerId).getFolderMessages(['starred':false])
				break
			case 'result':
				def activityInstance = getActivityInstance()
				def messageOwners = activityInstance? Fmessage.getMessageOwners(activityInstance): null
				messageInstanceList = Fmessage.searchMessages(params.searchString, Group.get(params.groupId), messageOwners).list()
				println "list: $messageInstanceList"
				break
			default:
				println "default"
				messageInstanceList = Fmessage.findAll()
				break
		}
		generateReport(messageInstanceList)
	}
	
	private def generateReport(messageInstanceList) {
		def currentTime = new Date()
		def formatedTime = dateToString(currentTime)
		List fields = ["id", "src", "dst", "text", "dateCreated"]
		Map labels = ["id":"DatabaseID", "src":"Source", "dst":"Destination", "text":"Text", "dateReceived":"Date"]
		Map parameters = [title: "FrontlineSMS Message Export"]
		response.setHeader("Content-disposition", "attachment; filename=FrontlineSMS_Export_${formatedTime}.${params.format}")
		try{
			exportService.export(params.format, response.outputStream, messageInstanceList, fields, labels, [:],parameters)
		} catch(Exception e){
			render(text: "Error creating report")
		}
		[messageInstanceList: messageInstanceList]
	}
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll: Folder
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
	
	private def getActivityDescription() {
		if(params.ownerId){
			String name
		 	switch(params.messageSection) {
				case 'poll':
					def poll = Poll.findById(params.ownerId)
					name = "${poll.title} (${poll.countMessages(false)} Messages)"
					break
				case 'folder':
					def folder = Folder.findById(params.ownerId)
					name = "${folder.name} (${folder.countMessages(false)} Messages)"
					break
			}
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
