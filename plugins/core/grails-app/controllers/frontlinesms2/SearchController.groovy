package frontlinesms2

import grails.util.GrailsConfig


class SearchController extends MessageController {
	
	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.sort = params.sort ?: 'date'
		return true
	}
	
	def index = { redirect(action:'result', params:params)}
	
	def no_search = {
		[groupInstanceList : Group.findAll(),
				folderInstanceList: Folder.findAll(),
				activityInstanceList: Activity.findAll(),
				messageSection: 'result',
				customFieldList : CustomField.getAllUniquelyNamed()]
	}

	def result = {
		def search = withSearch { searchInstance ->
			def activity =  getActivityInstance()
			searchInstance.owners = activity ? [activity] : null
			searchInstance.searchString = params.searchString ?: ""
			searchInstance.contactString = params.contactString ?: null
			searchInstance.group = params.groupId ? Group.get(params.groupId) : null
			searchInstance.status = params.messageStatus ?: null
			searchInstance.activityId = params.activityId ?: null
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
		String searchDescriptor = message(code: 'searchdescriptor.searching')
		if(search.searchString) {
			searchDescriptor += ' "'+search.searchString+'"'
		} else {
			searchDescriptor += message(code: 'searchdescriptor.all.messages')
		}
		 
		if(search.group) searchDescriptor += ", "+search.group.name
		if(search.owners) {
			def activity = getActivityInstance()
			String ownerDescription = activity.name
			searchDescriptor += ", "+ownerDescription
		}
		searchDescriptor += search.inArchive? message(code: 'searchdescriptor.archived.messages'):message(code: 'searchdescriptor.exclude.archived.messages') 
		if(search.contactString) searchDescriptor += ", "+search.contactString
		if (search.customFields.find{it.value}) {
			search.customFields.find{it.value}.each{
				searchDescriptor += ", "+it.key+"="+it.value
			}
		}
		if(search.status) {
			searchDescriptor += message(code: 'searchdescriptor.only') + search.status
		}
		if(search.startDate && search.endDate){
			searchDescriptor += message(code: 'searchdescriptor.between') + search.startDate + message(code: 'searchdescriptor.and') + search.endDate
		} else if (search.startDate) {
			searchDescriptor += message(code: 'searchdescriptor.from') + search.startDate
		} else if (search.endDate) {
			searchDescriptor += message(code: 'searchdescriptor.until') + search.endDate
		}
		return searchDescriptor
	}
	
	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'activity'? Activity : MessageOwner
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
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
