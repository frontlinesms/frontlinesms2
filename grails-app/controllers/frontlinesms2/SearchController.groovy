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
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		true
	}
	
	def result = {
		def search
		if(params.searchId) {
			search = Search.findById(params.searchId)
		} else {
			search = Search.findByName("TempSearchObject") ?: new Search(name: "TempSearchObject")
			def activity =  getActivityInstance()
			search.owners = activity ? Fmessage.getMessageOwners(activity): null
			search.searchString = params.searchString?: ""
			search.contactString = params.contactString?: null
			search.group = params.groupId ? Group.get(params.groupId) : null
			search.status = params.messageStatus ?: null
			search.activityId = params.activityId ?: null
			search.activity =  getActivityInstance()
			search.inArchive = params.inArchive ? true : false
			search.save(failOnError: true, flush: true)
		}
		
		def rawSearchResults = Fmessage.search(search)
		def searchResults = rawSearchResults.list(sort:"dateReceived", order:"desc", max: params.max, offset: params.offset)
		def searchDescription = getSearchDescription(search)
		def checkedMessageCount = params.checkedMessageList?.tokenize(',')?.size()
		[searchDescription: searchDescription,
				search: search,
				checkedMessageCount: checkedMessageCount,
				messageInstanceList: searchResults,
				messageInstanceTotal: rawSearchResults.count()] << 
			show(searchResults) << no_search()
	}

	def show = { searchResults ->
		def messageInstance = params.messageId ? Fmessage.get(params.messageId) :searchResults[0]
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		[messageInstance: messageInstance]
	}
	
	private def getSearchDescription(search) {
		String searchDescriptor = "Searching in "
		if(!search.owners && !search.group && !search.contactString) {
			searchDescriptor += "all messages"
		} else {
			if(search.contactString) searchDescriptor += "${search.contactString}"
			if(search.group) searchDescriptor += " '${search.group.name}'"
			if(search.owners) {
				def activity = getActivityInstance()
				String ownerDescription = activity instanceof Poll ? activity.title: activity.name
				searchDescriptor += " '$ownerDescription'"
			}
		}
		return searchDescriptor
	}
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll : Folder
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
