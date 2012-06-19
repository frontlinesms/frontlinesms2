package frontlinesms2

import grails.util.GrailsConfig

class SearchController extends MessageController {
	def beforeInterceptor = {
		params.offset  = params.offset ?: 0
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.sort = params.sort ?: 'date'
		return true
	}
	
	def index() { redirect(action:'no_search', params:params) }
	
	def no_search() {
		[groupInstanceList:Group.findAll(),
				folderInstanceList:Folder.findAll(),
				activityInstanceList:Activity.findAll(),
				messageSection:'result',
				customFieldList:CustomField.getAllUniquelyNamed()]
	}
	
	def result = {
		def search = new SearchCommand()
		def activity = getActivityInstance()
		search.owners = activity ? [activity] : null
		search.searchString = params.searchString ?: ""
		search.contactString = params.contactString ?: null
		search.group = params.groupId ? Group.get(params.groupId) : null
		search.status = params.messageStatus ?: null
		search.activityId = params.activityId ?: null
		search.inArchive = params.inArchive ? true : false
		search.startDate = params.startDate ?: null
		search.endDate = params.endDate ?: null
		search.customFields = [:]

		CustomField.getAllUniquelyNamed().each { customFieldName ->
			if(params[customFieldName])
				search.customFields[customFieldName] = params[customFieldName]
		}

		def rawSearchResults = Fmessage.search(search)
		def searchResults = rawSearchResults.list(sort:"date", order:"desc", max: params.max, offset: params.offset)
		def searchDescription = getSearchDescription(search)
		def checkedMessageCount = params.checkedMessageList?.tokenize(',')?.size()
		flash.message = params.flashMessage
		[searchDescription: searchDescription,
				search: search,
				checkedMessageCount: checkedMessageCount,
				messageInstanceList: searchResults,
				messageInstanceTotal: rawSearchResults.count()] << show() << no_search()
	}

	def show() {
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
			searchDescriptor += " \"$search.searchString\""
		} else {
			searchDescriptor += (" " + message(code:'searchdescriptor.all.messages'))
		}
		 
		if(search.group) searchDescriptor += ", $search.group.name"
		if(search.owners) {
			def activity = getActivityInstance()
			String ownerDescription = activity.name
			searchDescriptor += ", $ownerDescription"
		}
		searchDescriptor += message(code:
				search.inArchive? 'searchdescriptor.archived.messages': 'searchdescriptor.exclude.archived.messages')
		if(search.contactString) searchDescriptor += ", $search.contactString"
		search.customFields?.findAll { it.value }?.each { f ->
			searchDescriptor += ", $f.key=$f.value"
		}
		if(search.status) { 
			searchDescriptor += message(code:'searchdescriptor.only', args:[search.status])
		}
		if(search.startDate && search.endDate){
			searchDescriptor += message(code:'searchdescriptor.between', args:[search.startDate, search.endDate])
		} else if(search.startDate) {
			searchDescriptor += message(code:'searchdescriptor.from', args:[search.startDate])
		} else if(search.endDate) {
			searchDescriptor += message(code:'searchdescriptor.until', args:[search.endDate])
		}
		return searchDescriptor
	}

	private def getActivityInstance() {
		if(params.activityId) {
			def stringParts = params.activityId.tokenize('-')
			def activityType = stringParts[0] == 'activity'? Activity: MessageOwner
			def activityId = stringParts[1]
			activityType.findById(activityId)
		} else return null
	}
}

class SearchCommand {
	String name
	String searchString
	String contactString
	List owners
	String activityId
	Group group
	String status
	Date startDate
	Date endDate
	Map customFields
	boolean inArchive
	
	static constraints = {
		name(blank: false, nullable: false, maxSize: 255)
		searchString(blank: true, maxSize: 255)
		contactString(blank: true, nullable: true, maxSize: 255)
		activityId(nullable: true, blank: true, maxSize: 255)
		owners(nullable: true)
		group(nullable: true)
		status(nullable: true)
		startDate(nullable: true)
		endDate(nullable: true)
		customFields(nullable: true)
		inArchive(nullable: true)
	}
}