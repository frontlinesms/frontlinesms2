package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat
import grails.util.GrailsConfig


class SearchController {
	
	def index = { redirect(action:'no_search') }
	
	def no_search = {
		[groupInstanceList : Group.findAll(),
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll(),
				messageSection: 'search']
	}

	def beforeInterceptor = {
		params['max'] = params.max?: GrailsConfig.config.grails.views.pagination.max
		params['offset']  = params['offset'] ?: 0
		true
	}
	
	def result = {
		def messageStatus = params.messageStatus
		params.groupInstance = params.groupId? Group.get(params.groupId): null
		params.activityInstance =  getActivityInstance()
		params.messageOwner = params.activityInstance? Fmessage.getMessageOwners(activityInstance): null
		params.messageStatus = messageStatus ? messageStatus.tokenize(",")*.trim() : null
		def searchResults = Fmessage.search(params)
		[searchDescription: getSearchDescription(),
				searchString: params.searchString,
				contactInstance: params.contactSearchString,
				groupInstance: params.groupInstance,
				activityId: params.activityId,
				messageInstanceList: searchResults,
				messageInstanceTotal: Fmessage.countAllSearchMessages(params),
				messageStatus: messageStatus] << show(searchResults) << no_search()
	}

	def show = { searchResults ->
		def messageInstance = params.messageId ? Fmessage.get(params.messageId) :searchResults[0]
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		[messageInstance: messageInstance]
	}
	
	def deleteMessage = {
		withFmessage { messageInstance ->
			messageInstance.toDelete()
			messageInstance.save(failOnError: true, flush: true)
			Fmessage.get(params.messageId).messageOwner?.refresh()
			flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: 'Fmessage'), messageInstance.id])}"
			params.remove('messageId')
			redirect(action: result, params:params)
		}
	}
	
	private def getSearchDescription() {
		def searchString = params.searchString
		def contact = params.contactSearchString
		def group = params.groupInstance
		def activity = params.activityInstance
		if(!searchString && !contact && !group && !activity) {
			null
		} else {
			String searchDescriptor = "Searching in "
			if(!activity && !group && !contact) {
				searchDescriptor += "all messages"
			} else {
				if(contact) searchDescriptor += "$contact"
				if(group) searchDescriptor += " '${group.name}'"
				if(activity) {
					String activityDescription = activity instanceof Poll? activity.title: activity.name
					searchDescriptor += " '$activityDescription'"
				}
			}
			return searchDescriptor
		}
	}
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll: Folder
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
	
	private def withFmessage(Closure c) {
		def m = Fmessage.get(params.messageId)
		if(m) c.call(m)
		else render(text: "Could not find message with id ${params.messageId}") // TODO handle error state properly
	}
}
