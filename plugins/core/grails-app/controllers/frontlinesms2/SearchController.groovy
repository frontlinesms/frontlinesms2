package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date;

import grails.util.GrailsConfig


class SearchController {
	
	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.sort = params.sort ?: 'date'
		true
	}
	
	def index = { redirect(action:'no_search') }
	
	def no_search = {
		[groupInstanceList : Group.findAll(),
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll(),
				messageSection: 'result',
				announcementInstanceList: Announcement.findAll(),
				customFieldList : CustomField.getAllUniquelyNamed()]
	}

	def result = {
		def search = withSearch { searchInstance ->
			def activity =  getActivityInstance()
			searchInstance.owners = activity ? (activity instanceof Poll ? activity.responses : [activity]) : null
			searchInstance.searchString = params.searchString ?: ""
			searchInstance.contactString = params.contactString ?: null
			searchInstance.group = params.groupId ? Group.get(params.groupId) : null
			searchInstance.status = params.messageStatus ?: null
			searchInstance.activityId = params.activityId ?: null
			searchInstance.activity =  getActivityInstance()
			searchInstance.inArchive = params.inArchive ? true : false
			searchInstance.startDate = params.startDate ?: null
			searchInstance.endDate = params.endDate ?: null
			searchInstance.customFields = [:]
			CustomField.getAllUniquelyNamed().each() { customFieldName ->
				if(params[customFieldName])
					searchInstance.customFields[customFieldName] = params[customFieldName]
			}
			searchInstance.save(failOnError: true, flush: true)
		}
		
		def rawSearchResults = Fmessage.search(search)
		def searchResults = rawSearchResults.list(sort:"date", order:"desc", max: params.max, offset: params.offset)
		def searchDescription = getSearchDescription(search)
		def checkedMessageCount = params.checkedMessageList?.tokenize(',')?.size()
		[searchDescription: searchDescription,
				search: search,
				checkedMessageCount: checkedMessageCount,
				messageInstanceList: searchResults,
				messageInstanceTotal: rawSearchResults.count()] << show() << no_search()
	}

	def show = {
		def messageInstance = params.messageId ? Fmessage.get(params.messageId.toLong()) : null
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		[messageInstance: messageInstance]
	}
		
	private def getSearchDescription(search) {
		String searchDescriptor = "Searching"
		if(search.searchString) {
			searchDescriptor += ' "'+search.searchString+'"'
		} else {
			searchDescriptor += ' all messages'
		}
		 
		if(search.group) searchDescriptor += ", "+search.group.name
		if(search.owners) {
			def activity = getActivityInstance()
			String ownerDescription = activity instanceof Poll ? activity.title: activity.name
			searchDescriptor += ", "+ownerDescription
		}
		searchDescriptor += search.inArchive? ", archived messages":", without archived messages" 
		if(search.contactString) searchDescriptor += ", "+search.contactString
		if (search.customFields.find{it.value}) {
			search.customFields.find{it.value}.each{
				searchDescriptor += ", "+it.key+"="+it.value
			}
		}
		if(search.startDate && search.endDate){
			searchDescriptor += ", between " + search.startDate + " and " + search.endDate
		} else if (search.startDate) {
			searchDescriptor += ", from " + search.startDate
		} else if (search.endDate) {
			searchDescriptor += ", until " + search.endDate
		}
		return searchDescriptor
	}
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'poll'? Poll : MessageOwner
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
	
	private def withFmessage(Closure c) {
		def m = Fmessage.get(params.messageId)
		if(m) c.call(m)
		else render(text: "Could not find message with id $params.messageId") // TODO handle error state properly
	}
	
	private def withSearch(Closure c) {
		def search = Search.get(params.searchId)
		if(search) {
			params.searchString = search.searchString
			params.contactString = search.contactString
			params.groupId = search.group
			params.messageStatus = search.status
			params.activityId = search.activityId
			params.inArchive = search.inArchive
			params.startDate = search.startDate
			params.endDate = search.endDate
			search.customFields.each() { customFieldName, val ->
				println customFieldName
				params[customFieldName] = val
			}
		}
		else {
			search = new Search(name: 'TempSearchObject')
		}
		c.call(search)
		search
	}
}
