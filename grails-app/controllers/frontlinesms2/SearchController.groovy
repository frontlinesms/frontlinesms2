package frontlinesms2

import java.text.DateFormat
import java.text.SimpleDateFormat
import grails.util.GrailsConfig


class SearchController {
	
	def dateFormatString = message(code:"default.search.date.format", default:"d/M/yyyy")
	def dateFormat = new SimpleDateFormat(dateFormatString)
	
	def index = { redirect(action:'no_search') }
	
	def no_search = {
		[groupInstanceList : Group.findAll(),
				folderInstanceList: Folder.findAll(),
				pollInstanceList: Poll.findAll(),
				messageSection: 'result',
				customFieldList : CustomField.getAllUniquelyNamed()]
	}

	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		true
	}
	
	def result = {
		def search = withSearch { searchInstance ->
			def activity =  getActivityInstance()
			searchInstance.owners = activity ? Fmessage.getMessageOwners(activity): null
			searchInstance.searchString = params.searchString?: ""
			searchInstance.contactString = params.contactString?: null
			searchInstance.group = params.groupId ? Group.get(params.groupId) : null
			searchInstance.status = params.messageStatus ?: null
			searchInstance.activityId = params.activityId ?: null
			searchInstance.activity =  getActivityInstance()
			searchInstance.inArchive = params.inArchive ? true : false
			searchInstance.startDate = params.startDate?: null
			searchInstance.startDate?.clearTime()
			searchInstance.endDate = params.endDate?: null
			searchInstance.endDate?.clearTime()
			searchInstance.customFields = [:]

			CustomField.getAllUniquelyNamed().each() {
				searchInstance.customFields[it] = params[it+'CustomField'] ?: null
			} 
			searchInstance.save(failOnError: true, flush: true)
		}

		//FIXME Need to combine the 2 search part (the name matching custom field and the message matching all criteria) in one service or domain
		def contactNameMatchingCustomField
		if (search.customFields.find{it.value}) {
			contactNameMatchingCustomField = CustomField.getAllContactNameMatchingCustomField(search.customFields)
		}
		def rawSearchResults = Fmessage.search(search, contactNameMatchingCustomField)
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
		def messageInstance = params.messageId ? Fmessage.get(params.messageId) : searchResults[0]
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
		 
		if(search.group) searchDescriptor += ", in "+search.group.name
		if(search.owners) {
			def activity = getActivityInstance()
			String ownerDescription = activity instanceof Poll ? activity.title: activity.name
			searchDescriptor += ", in"+ownerDescription
		}
		searchDescriptor += search.inArchive? ", include archived messages":", without archived messages" 
		if(search.contactString) searchDescriptor += ", with contact name="+search.contactString
		if (search.customFields.find{it.value}) {
			search.customFields.find{it.value}.each{
				searchDescriptor += ", with contact having "+it.key+"="+it.value
			}
		}
		if(search.startDate && search.endDate){
			searchDescriptor += ", between " + search.startDate.format(dateFormatString) + " and " + search.endDate.format(dateFormatString) 
		} else if (search.startDate) {
			searchDescriptor += ", from the " + search.startDate.format(dateFormatString)
		} else if (search.endDate) {
			searchDescriptor += ", until the " + search.endDate.format(dateFormatString)
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
			c.call(search)
		}
		else {
			search = new Search(name: "TempSearchObject")
			c.call(search)
		}
	}
}
